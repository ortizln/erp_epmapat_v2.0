package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Facturamodificaciones;
import com.erp.comercializacion.repositories.FacturamodificacionesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturamodificacionesService {
    @Autowired
    private FacturamodificacionesR dao;

    public List<Facturamodificaciones> findAll_modi() {
        return dao.findAll();
    }
    public <S extends Facturamodificaciones> S save_modi(S entity) {
        return dao.save(entity);
    }
}
