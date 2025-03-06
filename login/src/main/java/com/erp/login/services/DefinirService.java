package com.erp.login.services;

import com.erp.login.models.Definir;
import com.erp.login.repositories.DefinirR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DefinirService {
    @Autowired
    DefinirR dao;

    public Optional<Definir> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Definir> S save(S entity) {
        return dao.save(entity);
    }
}
