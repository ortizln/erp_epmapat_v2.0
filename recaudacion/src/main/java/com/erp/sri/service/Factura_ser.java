package com.erp.sri.service;

import com.erp.sri.interfaces.Interes_int;
import com.erp.sri.interfaces.NroFactura_int;
import com.erp.sri.model.Facturas;
import com.erp.sri.repository.Cajas_rep;
import com.erp.sri.repository.Facturas_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class Factura_ser {
    @Autowired
    private Facturas_rep dao;
    @Autowired
    private Cajas_rep cj_dao;
    @Autowired
    private Recaudaxcaja_ser rxc_service ;
    public List<Interes_int> getForIntereses(Long idfactura) {
        return dao.getForIntereses(idfactura);
    }
    public <S extends Facturas> Facturas cobrarFactura(S factura){
        if(factura.getNrofactura() == null){
            NroFactura_int _nroFactura = cj_dao.buildNroFactura(factura.getUsuariocobro());
            String secuencial = fSecuencial(_nroFactura.getSecuencial());
            String puntoEmision = fPemiCaja(_nroFactura.getEstablecimiento());
            String codigo = fPemiCaja(_nroFactura.getCodigo());
            String nrofactura = puntoEmision + '-' + codigo +'-'+ secuencial;
            rxc_service.updateLastfactFinFac(factura.getUsuariocobro(), Long.valueOf(secuencial));
            factura.setNrofactura(nrofactura);
        }
        return dao.save(factura);
    }
    public Optional<Facturas> findFacturaById(Long idfactura){
        return dao.findById(idfactura);
    }

    public static String fSecuencial(Long numero) {
        String formato = "%09d";
        return String.format(formato, numero);
    }
    public static String fPemiCaja(Long numero) {
        String formato = "%03d";
        return String.format(formato, numero);
    }
    public BigDecimal getValorACobrar(Long idfactura){
        return dao.getValorACobrar(idfactura);
    }
}
