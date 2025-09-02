package com.erp.comercializacion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.Formacobro;
import com.erp.comercializacion.repositories.FormacobroR;

@Service
public class FormacobroServicio {

    @Autowired
    private FormacobroR dao;

    public List<Formacobro> findAll() {
        return dao.findAll();
    }

}
