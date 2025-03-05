package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Ubicacionm;
import com.erp.comercializacion.repositories.UbicacionmR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UbicacionmService {
    @Autowired
    private UbicacionmR dao;

    public <S extends Ubicacionm> S save(S entity) {
        return dao.save(entity);
    }

    public List<Ubicacionm> findAll() {
        return dao.findAll();
    }

    public Optional<Ubicacionm> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

}
