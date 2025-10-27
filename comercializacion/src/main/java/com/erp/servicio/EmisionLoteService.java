package com.erp.servicio;

import com.erp.DTO.CalculoDetalle;
import com.erp.DTO.EmisionOfCuentaDTO;
import com.erp.DTO.TarifasConst;
import com.erp.interfaces.DefinirProjection;
import com.erp.interfaces.EmisionesInterface;
import com.erp.modelo.*;
import com.erp.modelo.administracion.Definir;
import com.erp.repositorio.*;
import com.erp.repositorio.administracion.DefinirR;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class EmisionLoteService {

    private static final int PAGE_SIZE = 500; // ajusta a tu infra
    private static final List<Long> RUBROS_CALCULADOS =
            List.of(TarifasConst.RUBRO_AP, TarifasConst.RUBRO_ALC, TarifasConst.RUBRO_SAN,
                    TarifasConst.RUBRO_CF, TarifasConst.RUBRO_EXC, TarifasConst.RUBRO_INT);

    // Porcentajes residenciales (inyecta tu arreglo existente)
    private static final BigDecimal[] PORC_RESIDENCIAL = {
            BigDecimal.valueOf(0.777), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78),
            BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.778), BigDecimal.valueOf(0.778),
            BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.78), BigDecimal.valueOf(0.68),
            BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.68),
            BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.68),
            BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.676), BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.678),
            BigDecimal.valueOf(0.678), BigDecimal.valueOf(0.68), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.647), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.65),
            BigDecimal.valueOf(0.65), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7),
            BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7), BigDecimal.valueOf(0.7)
    };
    @Autowired private EmisionesR daoEmisiones;
    @Autowired private FacturasR daoFacturas;
    @Autowired private RubroxfacR daoRubroxfac;
    @Autowired private DefinirR daoDefinir;
    @Autowired private Pliego24R daoPliego;
    @Autowired private CategoriaR daoCategoria;


    // Cachés por ejecución
    private final Map<Integer, Categorias> cacheCategoria = new HashMap<>();
    private final Map<String, Pliego24> cachePliego = new HashMap<>();

    @Transactional
    public void recalcularEmision(Long idemision) {
        System.out.println("Recalculando ");
        int page = 0;
        Page<EmisionesInterface> p;

        do {
            p = daoEmisiones.getSWalcatarilladosPaginado(idemision, PageRequest.of(page++, PAGE_SIZE));
            if (p.isEmpty()) break;

            // 1) Cargar facturas de la página
            List<Long> idsFactura = p.map(EmisionesInterface::getIdfactura).toList();
            Map<Long, Facturas> facturas = daoFacturas.findAllById(idsFactura)
                    .stream().collect(Collectors.toMap(Facturas::getIdfactura, f -> f));

            // 2) Preparar resultados en memoria
            List<Rubroxfac> rubrosNuevos = new ArrayList<>(p.getNumberOfElements() * 6);
            List<Facturas> facturasAguardar = new ArrayList<>(p.getNumberOfElements());

            for (EmisionesInterface e : p) {
                Facturas factura = facturas.get(e.getIdfactura());
                if (factura == null) continue;
                System.out.println("Emision: "+ e.getCuenta());
                EmisionOfCuentaDTO dto = buildDto(e, factura);
                BigDecimal multa = calcularMulta(dto.getCuenta());

                CalculoDetalle det = calcular(dto, multa);

                rubrosNuevos.add(nuevoRubro(factura, TarifasConst.RUBRO_AP,  det.ap()));
                rubrosNuevos.add(nuevoRubro(factura, TarifasConst.RUBRO_ALC, det.alc()));
                rubrosNuevos.add(nuevoRubro(factura, TarifasConst.RUBRO_SAN, det.san()));
                rubrosNuevos.add(nuevoRubro(factura, TarifasConst.RUBRO_CF,  det.cf()));
                if (det.exc().signum() > 0)
                    rubrosNuevos.add(nuevoRubro(factura, TarifasConst.RUBRO_EXC, det.exc()));
                if (det.multa().signum() > 0)
                    rubrosNuevos.add(nuevoRubro(factura, TarifasConst.RUBRO_INT, det.multa()));

                factura.setTotaltarifa(det.total());
                factura.setValorbase(det.total());
                factura.setFeccrea(LocalDate.now().withDayOfMonth(1));
                facturasAguardar.add(factura);
            }

            // 3) Persistencia por página (borrar + saveAll)
            for (Long idf : idsFactura) {
                daoRubroxfac.deleteRubrosFactura(idf, RUBROS_CALCULADOS);
            }
            if (!rubrosNuevos.isEmpty()) daoRubroxfac.saveAll(rubrosNuevos);
            if (!facturasAguardar.isEmpty()) daoFacturas.saveAll(facturasAguardar);

        } while (p.hasNext());
    }

    // --------- Helpers ---------

    private EmisionOfCuentaDTO buildDto(EmisionesInterface e, Facturas factura) {
        EmisionOfCuentaDTO dto = new EmisionOfCuentaDTO();
        dto.setCuenta(e.getCuenta());
        dto.setIdfactura(e.getIdfactura());
        dto.setM3(e.getM3());
        System.out.println("M3: " + e.getM3());
        if(dto.getM3() < 0){
            dto.setM3(0);
        }
        // “rebote” por regla adulto mayor cat 9 > 70 pasa a cat 2
        int cat = (e.getCategoria() == 1 || (e.getCategoria() == 9 && e.getSwAdultoMayor() && e.getM3() > 70))
                ? 2 : e.getCategoria();
        dto.setCategoria(cat);

        dto.setSwMunicipio(e.getSwMunicipio());
        dto.setSwAdultoMayor(e.getSwAdultoMayor());
        dto.setSwAguapotable(e.getSwAguapotable());
        dto.setFactura(factura);

        dto.setPliego24(getPliegoCached(cat, e.getM3()));
        dto.setCategorias(getCategoriaCached(cat));
        return dto;
    }

    private Categorias getCategoriaCached(int categoriaId) {
        return cacheCategoria.computeIfAbsent(categoriaId, daoCategoria::getCategoriaById);
    }

    private Pliego24 getPliegoCached(int categoria, int m3) {
        String key = categoria + ":" + m3; // si tu _findBloque usa “bloques”, normaliza key al bloque
        return cachePliego.computeIfAbsent(key, k -> daoPliego._findBloque(categoria, m3));
    }

    private BigDecimal calcularMulta(Long cuenta) {
        List<Long> idfacturas = daoFacturas.findSinCobroAbo(cuenta);
        long pendientes = idfacturas.size();
        if (pendientes <= 2) return BigDecimal.ZERO;
        DefinirProjection def = daoDefinir.findDefinirWithoutFirma(1L);
        return (def != null && def.getRbu() != null)
                ? def.getRbu().multiply(new BigDecimal("0.005"), TarifasConst.MC)
                : BigDecimal.ZERO;
    }

    private Rubroxfac nuevoRubro(Facturas f, long idRubro, BigDecimal valor) {
        Rubroxfac r = new Rubroxfac();
        Rubros rubro = new Rubros(); rubro.setIdrubro(idRubro);
        r.setIdrubro_rubros(rubro);
        r.setIdfactura_facturas(f);
        r.setCantidad(1F);
        r.setValorunitario(valor);
        return r;
    }

    // ================== Motor de cálculo ==================

    private CalculoDetalle calcular(EmisionOfCuentaDTO dto, BigDecimal multa) {
        // % residencial si cat 1 o 9 (por m3)
        if(dto.getM3() < 0){
            dto.setM3(0);
        }
        System.out.println("Calculando: " + dto.getCuenta() );
        BigDecimal porcCatRes = null;
        if (dto.getCategoria() == 1 || dto.getCategoria() == 9) {
            int idx = Math.min(dto.getM3(), PORC_RESIDENCIAL.length - 1);
            porcCatRes = PORC_RESIDENCIAL[idx];
        }
        BigDecimal porc = (porcCatRes != null) ? porcCatRes : dto.getPliego24().getPorc();

        // Agua potable
        BigDecimal apFijo = dto.getCategorias().getFijoagua()
                .subtract(TarifasConst.TEN_CENTS, TarifasConst.MC)
                .multiply(porc, TarifasConst.MC);

        BigDecimal apVar = BigDecimal.valueOf(dto.getM3())
                .multiply(dto.getPliego24().getAgua(), TarifasConst.MC)
                .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);

        BigDecimal ap = apFijo.add(apVar, TarifasConst.MC);
        if (dto.getCategoria() == 4 && dto.isSwMunicipio()) ap = ap.multiply(TarifasConst.HALF, TarifasConst.MC);
        if (dto.getCategoria() == 9) ap = ap.multiply(TarifasConst.HALF, TarifasConst.MC);

        // Alcantarillado (si NO es swAguapotable)
        BigDecimal alc = BigDecimal.ZERO;
        if (!dto.isSwAguapotable()) {
            BigDecimal fijo = dto.getCategorias().getFijosanea()
                    .subtract(TarifasConst.FIFTY_CENTS, TarifasConst.MC)
                    .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);

            BigDecimal variable = BigDecimal.valueOf(dto.getM3())
                    .multiply(dto.getPliego24().getSaneamiento().multiply(TarifasConst.HALF, TarifasConst.MC), TarifasConst.MC)
                    .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);

            alc = fijo.add(variable, TarifasConst.MC);
            if (dto.getCategoria() == 4 && dto.isSwMunicipio()) alc = alc.multiply(TarifasConst.HALF, TarifasConst.MC);
            if (dto.getCategoria() == 9) alc = alc.multiply(TarifasConst.HALF, TarifasConst.MC);

            // Hidrosuccionador (0.50 * porc) se suma al final de alcantarillado
            alc = alc.add(TarifasConst.FIFTY_CENTS.multiply(dto.getPliego24().getPorc(), TarifasConst.MC), TarifasConst.MC);
        }

        // Saneamiento (si NO es swAguapotable)
        BigDecimal san = BigDecimal.ZERO;
        if (!dto.isSwAguapotable()) {
            san = BigDecimal.valueOf(dto.getM3())
                    .multiply(dto.getPliego24().getSaneamiento().multiply(TarifasConst.HALF, TarifasConst.MC), TarifasConst.MC)
                    .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);
            if (dto.getCategoria() == 4 && dto.isSwMunicipio()) san = san.multiply(TarifasConst.HALF, TarifasConst.MC);
            if (dto.getCategoria() == 9) san = san.multiply(TarifasConst.HALF, TarifasConst.MC);
        }

        // Conservación de fuentes (0.10)
        BigDecimal cf = new BigDecimal("0.10");

        // Excedente (solo cat 9 + adulto mayor + 34 < m3 ≤ 70)
        BigDecimal exc = BigDecimal.ZERO;
        if (dto.getCategoria() == 9 && dto.isSwAdultoMayor() && dto.getM3() > 34 && dto.getM3() <= 70) {
            exc = calcularExcedente(dto);
        }

        BigDecimal total = ap.add(alc, TarifasConst.MC)
                .add(san, TarifasConst.MC)
                .add(cf, TarifasConst.MC)
                .add(exc, TarifasConst.MC)
                .add(multa, TarifasConst.MC);

        // Redondear solo al final
        return new CalculoDetalle(
                ap.setScale(TarifasConst.SCALE_MONEY, RoundingMode.HALF_UP),
                alc.setScale(TarifasConst.SCALE_MONEY, RoundingMode.HALF_UP),
                san.setScale(TarifasConst.SCALE_MONEY, RoundingMode.HALF_UP),
                cf.setScale(TarifasConst.SCALE_MONEY, RoundingMode.HALF_UP),
                exc.setScale(TarifasConst.SCALE_MONEY, RoundingMode.HALF_UP),
                multa.setScale(TarifasConst.SCALE_MONEY, RoundingMode.HALF_UP),
                total.setScale(TarifasConst.SCALE_MONEY, RoundingMode.HALF_UP)
        );
    }

    private BigDecimal calcularExcedente(EmisionOfCuentaDTO base) {
        System.out.println("Calculando exedentes ");
        EmisionOfCuentaDTO dto1 = cloneForExcedente(base, base.getM3());
        EmisionOfCuentaDTO dto2 = cloneForExcedente(base, base.getM3() - 1);

        BigDecimal t1 = totalSinMultaNiExcedente(dto1);
        BigDecimal t2 = totalSinMultaNiExcedente(dto2);
        return t1.subtract(t2, TarifasConst.MC);
    }

    private EmisionOfCuentaDTO cloneForExcedente(EmisionOfCuentaDTO src, int m3) {
        EmisionOfCuentaDTO d = new EmisionOfCuentaDTO();
        d.setCuenta(src.getCuenta());
        d.setIdfactura(src.getIdfactura());
        d.setM3(m3);
        d.setCategoria(1); // tu regla para excedente
        d.setPliego24(getPliegoCached(1, m3));
        d.setCategorias(getCategoriaCached(1));
        d.setSwMunicipio(src.isSwMunicipio());
        d.setSwAdultoMayor(src.isSwAdultoMayor());
        d.setSwAguapotable(src.isSwAguapotable());
        d.setFactura(src.getFactura());
        return d;
    }

    private BigDecimal totalSinMultaNiExcedente(EmisionOfCuentaDTO dto) {
        BigDecimal porcCatRes = null;
        if (dto.getCategoria() == 1 || dto.getCategoria() == 9) {
            int idx = Math.min(dto.getM3(), PORC_RESIDENCIAL.length - 1);
            porcCatRes = PORC_RESIDENCIAL[idx];
        }
        BigDecimal porc = (porcCatRes != null) ? porcCatRes : dto.getPliego24().getPorc();

        BigDecimal apFijo = dto.getCategorias().getFijoagua()
                .subtract(TarifasConst.TEN_CENTS, TarifasConst.MC)
                .multiply(porc, TarifasConst.MC);

        BigDecimal apVar = BigDecimal.valueOf(dto.getM3())
                .multiply(dto.getPliego24().getAgua(), TarifasConst.MC)
                .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);

        BigDecimal ap = apFijo.add(apVar, TarifasConst.MC);
        if (dto.getCategoria() == 4 && dto.isSwMunicipio()) ap = ap.multiply(TarifasConst.HALF, TarifasConst.MC);
        if (dto.getCategoria() == 9) ap = ap.multiply(TarifasConst.HALF, TarifasConst.MC);

        BigDecimal alc = BigDecimal.ZERO;
        if (!dto.isSwAguapotable()) {
            BigDecimal fijo = dto.getCategorias().getFijosanea()
                    .subtract(TarifasConst.FIFTY_CENTS, TarifasConst.MC)
                    .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);

            BigDecimal variable = BigDecimal.valueOf(dto.getM3())
                    .multiply(dto.getPliego24().getSaneamiento().multiply(TarifasConst.HALF, TarifasConst.MC), TarifasConst.MC)
                    .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);

            alc = fijo.add(variable, TarifasConst.MC);
            if (dto.getCategoria() == 4 && dto.isSwMunicipio()) alc = alc.multiply(TarifasConst.HALF, TarifasConst.MC);
            if (dto.getCategoria() == 9) alc = alc.multiply(TarifasConst.HALF, TarifasConst.MC);

            alc = alc.add(TarifasConst.FIFTY_CENTS.multiply(dto.getPliego24().getPorc(), TarifasConst.MC), TarifasConst.MC);
        }

        BigDecimal san = BigDecimal.ZERO;
        if (!dto.isSwAguapotable()) {
            san = BigDecimal.valueOf(dto.getM3())
                    .multiply(dto.getPliego24().getSaneamiento().multiply(TarifasConst.HALF, TarifasConst.MC), TarifasConst.MC)
                    .multiply(dto.getPliego24().getPorc(), TarifasConst.MC);
            if (dto.getCategoria() == 4 && dto.isSwMunicipio()) san = san.multiply(TarifasConst.HALF, TarifasConst.MC);
            if (dto.getCategoria() == 9) san = san.multiply(TarifasConst.HALF, TarifasConst.MC);
        }

        BigDecimal cf = new BigDecimal("0.10");
System.out.println("Cuenta: "+dto.getCuenta()+" Valor: "+ap.add(alc, TarifasConst.MC).add(san, TarifasConst.MC).add(cf, TarifasConst.MC));
        return ap.add(alc, TarifasConst.MC).add(san, TarifasConst.MC).add(cf, TarifasConst.MC);
    }
}
