package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Impuestos;
import com.erp.comercializacion.repositories.ImpuestosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ImpuestosService {
    @Autowired
    private ImpuestosR dao;

    public Impuestos getCurrentImpuesto() {
        return dao.getCurrentImpuesto();
    }

}
