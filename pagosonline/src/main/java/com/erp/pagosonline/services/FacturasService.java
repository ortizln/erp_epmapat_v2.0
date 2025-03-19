package com.erp.pagosonline.services;

import com.erp.pagosonline.repositories.FacturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturasService {
    @Autowired
    private FacturasR dao;
    public Object[] findFacturasSinCobro(Long cuenta){
       return dao.findFacturasSinCobro(cuenta);
    }
}
