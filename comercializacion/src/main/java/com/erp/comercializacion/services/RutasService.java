package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.CuentasByRutas;
import com.erp.comercializacion.models.Rutas;
import com.erp.comercializacion.repositories.RutasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RutasService {

    @Autowired
    private RutasR dao;

    public List<Rutas> findAll() {
        return dao.findAll();
    }

    public List<Rutas> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public List<Rutas> findAllActive() {
        return dao.findAllActive();
    }
    public Optional<Rutas> findById(Long id) {
        return dao.findById(id);
    }
    public <S extends Rutas> S save(S entity) {
        return dao.save(entity);
    }
    public void delete(Rutas entity) {
        dao.delete(entity);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
    public boolean valCodigo(String codigo) {
        return dao.valCodigo(codigo);
    }
    public List<CuentasByRutas> getNcuentasByRutas() {
        return dao.getNcuentasByRutas();
    }
}
