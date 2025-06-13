package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Ifinan;
import com.erp.comercializacion.repositories.IfinanR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class IfinanService {
    @Autowired
    IfinanR dao;

    public List<Ifinan> findByCodifinan(String codifinan) {
        return dao.findByCodifinan( codifinan );
    }

    public List<Ifinan> findByNomifinan(String nomifinan) {
        return dao.findByNomifinan( nomifinan );
    }

    public <S extends Ifinan> S save(S entity) {
        return dao.save(entity);
    }

    public List<Ifinan> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public Page<Ifinan> findAll(Pageable pageable) {
        return dao.findAll(pageable);
    }

    public Optional<Ifinan> findById(Long id) {
        return dao.findById(id);
    }

    // public Boolean deleteById(Long id) {
    //    if (dao.existsById(id)) {
    //       dao.deleteById(id);
    //       return !dao.existsById(id);
    //    }
    //    return false;
    // }

    public void delete(Ifinan entity) {
        dao.delete(entity);
    }
}
