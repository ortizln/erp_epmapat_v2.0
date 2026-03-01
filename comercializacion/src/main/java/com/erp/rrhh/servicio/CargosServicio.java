package com.erp.rrhh.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.rrhh.modelo.Cargos;
import com.erp.rrhh.repositorio.CargosR;

@Service
public class CargosServicio {
    @Autowired
    private CargosR dao;

    public List<Cargos> findAll() {
        return dao.findAll();
    }

    public Cargos save(Cargos cargos) {
        return dao.save(cargos);
    }
}

