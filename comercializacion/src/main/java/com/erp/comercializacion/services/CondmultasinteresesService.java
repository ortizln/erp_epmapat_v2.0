package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Condmultasintereses;
import com.erp.comercializacion.repositories.CondmultasinteresesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CondmultasinteresesService {
    @Autowired
    private CondmultasinteresesR dao;

    public <S extends Condmultasintereses> S save(S entity) {
        return dao.save(entity);
    }
}
