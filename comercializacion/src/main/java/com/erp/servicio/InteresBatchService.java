package com.erp.servicio;

import com.erp.interfaces.FacLite;
import com.erp.modelo.Intereses;
import com.erp.modelo.Tmpinteresxfac;
import com.erp.repositorio.FacturasR;
import com.erp.repositorio.InteresesR;
import com.erp.repositorio.TmpinteresxfacR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class InteresBatchService {

    @Autowired
    private FacturasR facturasR;          // proyección de facturas sin cobrar
    @Autowired private TmpinteresxfacR tmpRepo;      // upsert en lotes
    @Autowired private InteresesR interesesR;        // tu repo de % mensuales

    private static final MathContext MC = new MathContext(16, RoundingMode.HALF_UP);
    private static final int SCALE_MONEY = 6;
    private static final int SCALE_PERCENT = 10;

    public Map<String,Object> recalcularIntereses(LocalDate fechaCorte, int LAG_MESES) {
        // 1) Traer SOLO lo necesario de las facturas (usa una proyección)
        List<FacLite> facs = facturasR.getSinCobrarLite(); // id, suma(BigDecimal), formapago(Integer), feccrea(LocalDate), fechatransferencia(LocalDate)
        System.out.println(facs.size());
        if (facs.isEmpty()) {
            return Map.of("status",200,"totalFacturas",0,"message","Sin facturas");
        }

        // 2) Determinar rango global de meses
        YearMonth corteYM = YearMonth.from(fechaCorte).minusMonths(LAG_MESES); // hasta este mes
        YearMonth minYM = facs.stream()
                .map(f -> YearMonth.from( (f.getFormaPago() != null && f.getFormaPago()==4) ? f.getFecTransfer() : f.getFecCrea() ))
                .min(Comparator.naturalOrder())
                .orElse(corteYM);

        if (minYM.isAfter(corteYM)) {
            return Map.of("status",200,"totalFacturas",facs.size(),"message","Sin meses para capitalizar");
        }

        // 3) Cargar porcentajes UNA vez (por años) y construir lista ordenada de YearMonth
        List<YearMonth> meses = rangeInclusive(minYM, corteYM);
        Map<YearMonth, BigDecimal> pctMap = cargarPctPorRango(meses);

        // 4) Precomputar acumulados: cum[i] = cum[i-1] * (1 + ratioMes)
        BigDecimal[] cum = new BigDecimal[meses.size()];
        cum[0] = BigDecimal.ONE.multiply(BigDecimal.ONE.add(pctToRatio(pctMap.getOrDefault(meses.get(0), BigDecimal.ZERO)), MC), MC);
        for (int i=1; i<meses.size(); i++) {
            BigDecimal r = pctToRatio(pctMap.getOrDefault(meses.get(i), BigDecimal.ZERO));
            cum[i] = cum[i-1].multiply(BigDecimal.ONE.add(r, MC), MC);
        }
        // Mapa inverso YearMonth -> índice
        Map<YearMonth,Integer> idx = new HashMap<>(meses.size());
        for (int i=0;i<meses.size();i++) idx.put(meses.get(i), i);

        // 5) Precalcular factorDesdeInicio por cada inicio distinto presente en facturas
        int endIdx = idx.get(corteYM);
        Map<YearMonth, BigDecimal> factorDesdeInicio = new HashMap<>();
        for (YearMonth ymStart : facs.stream()
                .map(f->YearMonth.from((f.getFormaPago()!=null && f.getFormaPago()==4)? f.getFecTransfer(): f.getFecCrea()))
                .collect(Collectors.toSet())) {

            Integer sIdx = idx.get(ymStart);
            if (sIdx == null || sIdx > endIdx) {
                factorDesdeInicio.put(ymStart, BigDecimal.ONE); // sin interés
                continue;
            }
            BigDecimal numer = cum[endIdx];
            BigDecimal denom = (sIdx==0) ? BigDecimal.ONE : cum[sIdx-1];
            factorDesdeInicio.put(ymStart, numer.divide(denom, MC));
        }

        // 6) Calcular intereses O(1) por factura y acumular entidades para upsert en lotes
        int batchSize = 1000;
        int guardadas = 0, actualizadas = 0;
        List<Tmpinteresxfac> buffer = new ArrayList<>(batchSize);
        LocalDateTime now = LocalDateTime.now();

        // Pre-cargar existentes por idfactura para upsert eficiente
        Map<Long, Tmpinteresxfac> existentes = tmpRepo
                .findAllByIdfacturaIn(facs.stream().map(FacLite::getId).toList())
                .stream()
                .collect(Collectors.toMap(Tmpinteresxfac::getIdfactura, it -> it));

        for (FacLite f : facs) {
            YearMonth ymStart = YearMonth.from((f.getFecCrea()!=null && f.getFormaPago()==4)? f.getFecTransfer(): f.getFecCrea());
            BigDecimal factor = factorDesdeInicio.getOrDefault(ymStart, BigDecimal.ONE);
            BigDecimal interes = f.getSuma().multiply(factor.subtract(BigDecimal.ONE, MC), MC)
                    .setScale(SCALE_MONEY, RoundingMode.HALF_UP);

            Tmpinteresxfac e = existentes.getOrDefault(f.getId(), new Tmpinteresxfac());
            boolean isNew = (e.getIdfactura() == null);
            e.setIdfactura(f.getId());
            e.setInteresapagar(interes);
            e.setFeccorte(now);

            buffer.add(e);
            if (buffer.size() == batchSize) {
                tmpRepo.saveAll(buffer);
                guardadas += (int) buffer.stream().filter(x -> x.getIdfactura()==null).count(); // opcional
                actualizadas += buffer.size() - (int) buffer.stream().filter(x -> x.getIdfactura()==null).count();
                buffer.clear();
            }
        }
        if (!buffer.isEmpty()) {
            tmpRepo.saveAll(buffer);
        }

        return Map.of(
                "status", 200,
                "totalFacturas", facs.size(),
                "message", "OK",
                "desde", minYM.toString(),
                "hasta", corteYM.toString()
        );
    }

    // ---------- helpers ----------

    private static List<YearMonth> rangeInclusive(YearMonth from, YearMonth to) {
        List<YearMonth> out = new ArrayList<>();
        YearMonth cur = from;
        while (!cur.isAfter(to)) { out.add(cur); cur = cur.plusMonths(1); }
        return out;
    }

    private Map<YearMonth, BigDecimal> cargarPctPorRango(List<YearMonth> meses) {
        Map<YearMonth, BigDecimal> map = new HashMap<>();
        YearMonth desde = meses.get(0);
        YearMonth hasta = meses.get(meses.size()-1);
        // UNA query JPQL: findByRango(yStart,mStart, yEnd,mEnd)
        List<Intereses> rows = interesesR.findByRango(
                (long) desde.getYear(), (long) desde.getMonthValue(),
                (long) hasta.getYear(), (long) hasta.getMonthValue());

        for (Intereses it : rows) {
            YearMonth ym = YearMonth.of(it.getAnio().intValue(), it.getMes().intValue());
            map.put(ym, it.getPorcentaje()==null? BigDecimal.ZERO : it.getPorcentaje());
        }
        // Rellenar faltantes con 0%
        for (YearMonth ym : meses) map.putIfAbsent(ym, BigDecimal.ZERO);
        return map;
    }

    private static BigDecimal pctToRatio(BigDecimal pct) {
        return pct.divide(BigDecimal.valueOf(100), SCALE_PERCENT, RoundingMode.HALF_UP);
    }
}
