package com.erp.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.rrhh.Cargos;
import com.erp.repositorio.rrhh.CargosR;

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
