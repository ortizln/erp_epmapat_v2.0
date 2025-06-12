package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Formacobro;
import com.erp.comercializacion.repositories.FormacobroR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormacobroService {
    @Autowired
    private FormacobroR dao;

    public List<Formacobro> findAll() {
        return dao.findAll();
    }

}
