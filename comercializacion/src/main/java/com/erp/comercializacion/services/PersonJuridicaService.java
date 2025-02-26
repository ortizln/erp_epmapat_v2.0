package com.erp.comercializacion.services;

import com.erp.comercializacion.models.PersonJuridica;
import com.erp.comercializacion.repositories.PersonJuridicaR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonJuridicaService {
    @Autowired
    private PersonJuridicaR dao;

    public List<PersonJuridica> findAll() {
        return dao.findAll();
    }

    public <S extends PersonJuridica> S save(S entity) {
        return dao.save(entity);
    }

    public List<PersonJuridica> findAll(Sort sort) {
        return dao.findAll(sort);
    }

}
