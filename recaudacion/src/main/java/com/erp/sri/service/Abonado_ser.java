package com.erp.sri.service;

import com.erp.sri.interfaces.Abonado_int;
import com.erp.sri.interfaces.Factura_int;
import com.erp.sri.model.Factura_interes;
import com.erp.sri.model.Tmpinteresxfac;
import com.erp.sri.repository.Abonado_rep;
import com.erp.sri.repository.Facturas_rep;
import com.erp.sri.repository.TmpinteresxfacR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class Abonado_ser {
    @Autowired
    private Abonado_rep a_dao;
    @Autowired
    private Facturas_rep f_dao;
    @Autowired
    private Interes_ser s_interes;
    @Autowired
    private Impuestos_ser s_impuestos;
    @Autowired
    private TmpinteresxfacR tmpinteresxfacR;

    public List<Factura_int> findSinCobrarByAbonado(Long idabonado) {
        Abonado_int abonado = a_dao.findClienteInAbonado(idabonado);
        if (abonado == null) {
            System.out.println("Abonado no encontrado con el ID: " + idabonado);
            return Collections.emptyList();
        }

        List<Factura_int> facturas = new ArrayList<>();

        if (Objects.equals(abonado.getCliente(), abonado.getResponsable())) {
            facturas.addAll(f_dao.findSinCobrarByCuenta(idabonado));
            facturas.addAll(f_dao.findSincobroNotConsumo(abonado.getResponsable()));
        } else {
            facturas.addAll(f_dao.findSinCobrar(abonado.getCliente()));
            facturas.addAll(f_dao.findSinCobrar(abonado.getResponsable()));
        }

        return facturas;
    }

    public List<Factura_interes> addInteresToFactura(List<Factura_int> facturas) {
        Map<Long, BigDecimal> interesesMap = s_interes.interesesOfFacturas(facturas);
        Map<Long, BigDecimal> ivaMap = s_impuestos.calcularIvas(facturas);

        return facturas.stream()
                .map(f -> {
                    Long id = f.getIdfactura();
                    BigDecimal interes = (!Boolean.TRUE.equals(f.getSwcondonar()))
                            ? interesesMap.getOrDefault(id, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;

                    BigDecimal iva = ivaMap.getOrDefault(id, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal total = f.getTotal().setScale(2, RoundingMode.HALF_UP);

                    return new Factura_interes(
                            f.getIdfactura(),
                            f.getIdmodulo(),
                            total,
                            f.getIdcliente(),
                            f.getIdabonado(),
                            f.getFeccrea(),
                            f.getFormaPago(),
                            f.getEstado(),
                            f.getPagado(),
                            f.getSwcondonar(),
                            interes,
                            f.getfechacobro(),
                            f.getUsuariocobro(),
                            f.getNrofactura(),
                            iva,
                            f.getNombre(),
                            f.getDireccion()
                    );
                })
                .collect(Collectors.toList());
    }


    public static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
