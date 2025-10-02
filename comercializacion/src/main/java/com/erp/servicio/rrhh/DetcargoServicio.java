package com.erp.servicio.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.rrhh.Detcargo;
import com.erp.repositorio.rrhh.DetcargoR;

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
