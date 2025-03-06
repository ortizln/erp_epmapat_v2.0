package com.erp.login.services;

import com.erp.login.models.Tabla4;
import com.erp.login.repositories.Tabla4R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Tabla4Service {
    @Autowired
    private Tabla4R dao;

    public List<Tabla4> findAll() {
        return dao.findAll();
    }
    //Validar Comprobantes por código
    public List<Tabla4> findByTipocomprobante(String nomcomprobante) {
        return dao.findByTipocomprobante(nomcomprobante);
    }
    //Validar Comprobantes por Nombre
    public List<Tabla4> findByNomcomprobante(String nomcomprobante) {
        return dao.findByNomcomprobante(nomcomprobante);
    }

    // public List<Tabla4> findAll(Sort sort) {
    // 	return dao.findAll(sort);
    // }

    public <S extends Tabla4> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Tabla4> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public void delete(Tabla4 entity) {
        dao.delete(entity);
    }
}
