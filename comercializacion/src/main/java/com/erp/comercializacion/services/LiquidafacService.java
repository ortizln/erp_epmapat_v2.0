package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Liquidafac;
import com.erp.comercializacion.repositories.LiquidafacR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiquidafacService {

    @Autowired
    private LiquidafacR dao;

    public List<Liquidafac> findByIdfacturacion(Long idfacturacion) {
        return dao.findByIdfacturacion(idfacturacion);
    }

    public <S extends Liquidafac> S save(S entity) {
        return dao.save(entity);
    }
}
