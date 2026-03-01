package com.erp.rrhh.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.rrhh.modelo.Tpcontratos;
import com.erp.rrhh.repositorio.TpcontratosR;

@Service
public class TpcontratosServicio {
    @Autowired
    private TpcontratosR dao;

    public List<Tpcontratos> findAll() {
        return dao.findAll();
    }

    public Tpcontratos save(Tpcontratos tpcontratos) {
        return dao.save(tpcontratos);
    }
}

