package com.erp.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.interfaces.mobile.EstadomMobile;
import com.erp.modelo.Estadom;
import com.erp.repositorio.EstadomR;

@Service
public class EstadomServicio {

    @Autowired
    private EstadomR dao;

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

    public List<EstadomMobile> findAllEstadom() {
        return dao.findAllEstadom();
    }
}
