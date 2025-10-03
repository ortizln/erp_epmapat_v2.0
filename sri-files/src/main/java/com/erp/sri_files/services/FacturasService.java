package com.erp.sri_files.services;

import com.erp.sri_files.models.Facturas;
import com.erp.sri_files.repositories.FacturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturasService {
    @Autowired
    private FacturasR facturasR;
    public Boolean findByIdfactura(Long idfactura){
        boolean request = false;
        Facturas factura = facturasR.findByIdfactura(idfactura);
        if(factura.getPagado() == 1){
            request= true;
        }
        return request;
    }
}
