package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Tpidentifica;
import com.erp.comercializacion.repositories.TpidentificaR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TpidentificaService {
    @Autowired
    TpidentificaR dao;

    public <S extends Tpidentifica> S save(S entity) {
        return dao.save(entity);
    }

    public List<Tpidentifica> findAll() {
        return dao.findAll();
    }

    public Optional<Tpidentifica> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

}
