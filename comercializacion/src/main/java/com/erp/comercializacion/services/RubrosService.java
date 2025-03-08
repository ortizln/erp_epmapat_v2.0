package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Rubros;
import com.erp.comercializacion.repositories.RubrosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RubrosService {
    @Autowired
    private RubrosR dao;
    public List<Rubros> findByIdmodulo(Long idmodulo) {
        return dao.findByIdmodulo(idmodulo);
    }
    public List<Rubros> findEmision() {
        return dao.findEmision();
    }

    public List<Rubros> findAll() {
        return dao.findAll();
    }

    public List<Rubros> findByNombre(Long idmodulo, String descripcion) {
        return dao.findByNombre(idmodulo, descripcion);
    }

    public List<Rubros> findByModulo(Long idmodulo, String descripcion) {
        return dao.findByModulo(idmodulo, descripcion);
    }

    public Optional<Rubros> findById(Long id) {
        return dao.findById(id);
    }
    public <S extends Rubros> S save(S entity) {
        return dao.save(entity);
    }

    public Rubros findByIdRubro(Long idrubro) {
        return dao.findByIdRubro(idrubro);
    }

    public List<Rubros> findByName(String descripcion) {
        return dao.findByName(descripcion);
    }
}
