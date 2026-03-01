package com.erp.comercializacion.servicio.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.modelo.administracion.Erpmodulos;
import com.erp.comercializacion.repositorio.administracion.ErpmodulosR;

@Service
public class ErpmodulosServicio {
    @Autowired
    private ErpmodulosR dao;

    public List<Erpmodulos> findAll() {
        return dao.findAll();
    }
}

