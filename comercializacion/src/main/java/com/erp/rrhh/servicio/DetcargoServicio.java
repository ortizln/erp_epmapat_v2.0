package com.erp.rrhh.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.rrhh.modelo.Detcargo;
import com.erp.rrhh.repositorio.DetcargoR;

@Service
public class DetcargoServicio {
    @Autowired
    private DetcargoR dao;

    public List<Detcargo> findAll() {
        return dao.findAll();
    }

    public Detcargo save(Detcargo detcargo) {
        return dao.save(detcargo);
    }
}

