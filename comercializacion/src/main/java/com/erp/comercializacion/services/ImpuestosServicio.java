package com.erp.comercializacion.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.Impuestos;
import com.erp.comercializacion.repositories.ImpuestosR;

@Service
public class ImpuestosServicio {
    @Autowired
    private ImpuestosR dao;

    public Impuestos getCurrentImpuesto() {
        return dao.getCurrentImpuesto();
    }
}
