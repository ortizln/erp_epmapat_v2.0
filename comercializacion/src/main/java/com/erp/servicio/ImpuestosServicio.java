package com.erp.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.Impuestos;
import com.erp.repositorio.ImpuestosR;

@Service
public class ImpuestosServicio {
    @Autowired
    private ImpuestosR dao;

    public Impuestos getCurrentImpuesto() {
        return dao.getCurrentImpuesto();
    }
}
