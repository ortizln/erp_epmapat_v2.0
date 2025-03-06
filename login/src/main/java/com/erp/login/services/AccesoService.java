package com.erp.login.services;

import com.erp.login.models.Acceso;
import com.erp.login.repositories.AccesoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccesoService {
    @Autowired
    AccesoR dao;

    public List<Acceso> findAll() {
        return dao.findAll();
    }
}
