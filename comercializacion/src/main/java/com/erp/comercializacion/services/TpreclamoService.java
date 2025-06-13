package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Tpreclamo;
import com.erp.comercializacion.repositories.TpreclamoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TpreclamoService {

    @Autowired
    private TpreclamoR dao;
    public <S extends Tpreclamo> S save(S entity) {
        return dao.save(entity);
    }

    public List<Tpreclamo> findAll() {
        return dao.findAll();
    }

    public Optional<Tpreclamo> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
