package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Nacionalidad;
import com.erp.comercializacion.repositories.NacionalidadR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NacionalidadService {
    @Autowired
    private NacionalidadR dao;

    public List<Nacionalidad> findAll() {
        return dao.findAll();
    }

    public List<Nacionalidad> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public <S extends Nacionalidad> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Nacionalidad> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public List<Nacionalidad> findByDescription(String nombre) {
        return dao.findByDescription(nombre);
    }

    public List<Nacionalidad> used(Long id) {
        return dao.used(id);
    }
}
