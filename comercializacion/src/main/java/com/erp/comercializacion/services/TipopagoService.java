package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Tipopago;
import com.erp.comercializacion.repositories.TipopagoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipopagoService {
    @Autowired
    private TipopagoR dao;

    public <S extends Tipopago> S save(S entity) {
        return dao.save(entity);
    }

    public List<Tipopago> findAll() {
        return dao.findAll();
    }

    public Optional<Tipopago> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

}
