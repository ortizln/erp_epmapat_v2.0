package com.erp.sri.service;

import com.erp.sri.model.Facxrecauda;
import com.erp.sri.repository.Facxrecauda_rep;
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
