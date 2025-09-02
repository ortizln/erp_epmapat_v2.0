package com.erp.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.rrhh.Contemergencia;
import com.erp.repositorio.rrhh.ContemergenciasR;

@Service
public class ContemergenciaServicio {
    @Autowired
    private ContemergenciasR dao;

    public List<Contemergencia> findAll() {
        return dao.findAll();
    }

    public List<Contemergencia> findByContEmergencia(String nombre) {
        return dao.findByContEmergencia(nombre);
    }

    public Contemergencia save(Contemergencia ce) {
        return dao.save(ce);
    }
}
