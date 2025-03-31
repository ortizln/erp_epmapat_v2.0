package com.erp.comercializacion.services;

import com.erp.comercializacion.repositories.FacturacionR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturacionService {
 @Autowired
    private FacturacionR dao;
}
