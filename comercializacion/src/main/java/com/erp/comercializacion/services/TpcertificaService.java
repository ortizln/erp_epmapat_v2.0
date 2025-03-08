package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Tpcertifica;
import com.erp.comercializacion.repositories.TpcertificaR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TpcertificaService {
    @Autowired
    private TpcertificaR dao;

    public List<Tpcertifica> findAll() {
        return dao.findAll();
    }
}
