package com.erp.comercializacion.services;

import com.erp.comercializacion.repositories.FacturasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturasService {
    @Autowired
    private FacturasR dao;
}
