package com.erp.sri.service;

import com.erp.sri.interfaces.Rubroxfaciva_int;
import com.erp.sri.model.Impuestos;
import com.erp.sri.repository.Impuestos_rep;
import com.erp.sri.repository.Rubroxfac_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class Impuestos_ser {
    @Autowired
    private Impuestos_rep im_dao;
    @Autowired
    private Rubroxfac_rep rf_dao ;

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
