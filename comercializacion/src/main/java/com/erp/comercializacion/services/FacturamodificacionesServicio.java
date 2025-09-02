package com.erp.comercializacion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.Facturamodificaciones;
import com.erp.comercializacion.repositories.FacturamodificacionesR;

@Service
public class FacturamodificacionesServicio {
    @Autowired
    private FacturamodificacionesR dao;

    public List<Facturamodificaciones> findAll_modi() {
        return dao.findAll();
    }

    public <S extends Facturamodificaciones> S save_modi(S entity) {
        return dao.save(entity);
    }

}
