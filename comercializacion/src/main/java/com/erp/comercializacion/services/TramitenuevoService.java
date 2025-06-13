package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Tramitenuevo;
import com.erp.comercializacion.repositories.TramitenuevoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TramitenuevoService {
    @Autowired
    private TramitenuevoR dao;
    public List<Tramitenuevo> findAll() {
        return dao.findAll();
    }
    public List<Tramitenuevo> findAll(Sort sort) {
        return dao.findAll(sort);
    }
    public <S extends Tramitenuevo> S save(S entity) {
        return dao.save(entity);
    }
    public Optional<Tramitenuevo> findById(Long id) {
        return dao.findById(id);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
    public List<Tramitenuevo> findByIdAguaTramite(Long idaguatramite) {
        return dao.findByIdAguaTramite(idaguatramite);
    }
}
