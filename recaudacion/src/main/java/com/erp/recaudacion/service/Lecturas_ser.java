package com.erp.recaudacion.service;

import com.erp.recaudacion.interfaces.Interes_int;
import com.erp.recaudacion.repository.Lecturas_rep;
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
