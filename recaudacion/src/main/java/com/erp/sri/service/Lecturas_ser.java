package com.erp.sri.service;

import com.erp.sri.interfaces.Interes_int;
import com.erp.sri.repository.Lecturas_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Lecturas_ser {
    @Autowired
    private Lecturas_rep dao;

    public List<Interes_int> getForIntereses(Long idfactura) {
        return dao.getForIntereses(idfactura);
    }
}
