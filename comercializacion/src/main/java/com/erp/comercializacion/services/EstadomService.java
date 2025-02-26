package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Estadom;
import com.erp.comercializacion.repositories.EstadomR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EstadomService {

    @Autowired
    EstadomR dao;

    public <S extends Estadom> S save(S entity) {
        return dao.save(entity);
    }
    public List<Estadom> findAll() {
        return dao.findAll();
    }
    public Optional<Estadom> findById(Long id) {
        return dao.findById(id);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
