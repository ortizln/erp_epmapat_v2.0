package com.erp.recaudacion.service;

import com.erp.recaudacion.model.Facxrecauda;
import com.erp.recaudacion.repository.Facxrecauda_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Facxrecauda_ser {
    @Autowired
    private Facxrecauda_rep fr_dao;

    public Facxrecauda save (Facxrecauda fr) {
        return fr_dao.save(fr);
    }

}
