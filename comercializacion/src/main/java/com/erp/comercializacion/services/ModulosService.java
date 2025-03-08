package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Modulos;
import com.erp.comercializacion.repositories.ModulosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModulosService {
    @Autowired
    private ModulosR dao;

    public List<Modulos> findAll() {
        return dao.findByOrderByDescripcionAsc();
    }

    public List<Modulos> getTwoModulos() {
        return dao.getTwoModulos();
    }

    public Optional<Modulos> findById(Long id) {
        return dao.findById(id);
    }
}
