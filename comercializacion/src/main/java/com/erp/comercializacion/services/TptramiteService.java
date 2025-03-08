package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Tptramite;
import com.erp.comercializacion.repositories.TptramiteR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TptramiteService {
    @Autowired
    private TptramiteR dao;

    public List<Tptramite> findAll() {
        return dao.findAll();
    }

    public List<Tptramite> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public <S extends Tptramite> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Tptramite> findById(Long id) {
        return dao.findById(id);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
}
