package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Tipotramite;
import com.erp.comercializacion.repositories.TipotramiteR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipotramiteService {
    @Autowired
    private TipotramiteR dao;

    public List<Tipotramite> findAll() {
        return dao.findAll();
    }

    public List<Tipotramite> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public <S extends Tipotramite> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Tipotramite> findById(Long id) {
        return dao.findById(id);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
