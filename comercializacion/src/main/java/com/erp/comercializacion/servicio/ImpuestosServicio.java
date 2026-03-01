package com.erp.comercializacion.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.modelo.Impuestos;
import com.erp.comercializacion.repositorio.ImpuestosR;

@Service
public class ImpuestosServicio {
    @Autowired
    private ImpuestosR dao;

    public Impuestos getCurrentImpuesto() {
        return dao.getCurrentImpuesto();
    }
}


