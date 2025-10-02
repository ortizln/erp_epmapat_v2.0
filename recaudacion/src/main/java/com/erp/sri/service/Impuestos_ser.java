package com.erp.sri.service;

import com.erp.sri.interfaces.Factura_int;
import com.erp.sri.interfaces.Rubroxfaciva_int;
import com.erp.sri.model.Impuestos;
import com.erp.sri.repository.Impuestos_rep;
import com.erp.sri.repository.Rubroxfac_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class Impuestos_ser {
    @Autowired
    private Impuestos_rep im_dao;
    @Autowired
    private Rubroxfac_rep rf_dao ;

    public Map<Long, BigDecimal> calcularIvas(List<Factura_int> facturas) {
        Impuestos impuestoActivo = im_dao.getImpuestoByEstado(true); // 1 sola llamada
        double porcentajeIva = impuestoActivo.getValor() / 100.0;

        return facturas.parallelStream()
                .map(f -> {
                    Long id = f.getIdfactura();
                    List<Rubroxfaciva_int> rubros = rf_dao.getRubrosByIdFactura(id);

                    double iva = rubros.stream()
                            .filter(r -> Boolean.TRUE.equals(r.getIva()))
                            .mapToDouble(r -> r.getCantidad() * r.getValorunitario() * porcentajeIva)
                            .sum();

                    return Map.entry(id, BigDecimal.valueOf(iva).setScale(2, RoundingMode.HALF_UP));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }


    public BigDecimal calcularIva(Long idfactura){
        List<Rubroxfaciva_int> rubros = rf_dao.getRubrosByIdFactura(idfactura);
        double iva =0.00;
        Impuestos imp = im_dao.getImpuestoByEstado(true);
        for(Rubroxfaciva_int i: rubros){
            if(i.getIva()){
                iva += (i.getCantidad() * i.getValorunitario()) *((double) imp.getValor() /100);
            }
            else{
                iva += 0.00;
            }
        }
        return BigDecimal.valueOf(iva);

    }
}
