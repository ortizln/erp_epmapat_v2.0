package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Facxrecauda;
import com.erp.comercializacion.repositories.FacxrecaudaR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FacxrecaudaService {
    @Autowired
    private FacxrecaudaR dao;

    public <S extends Facxrecauda> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Facxrecauda> findById(Long id) {
        return dao.findById(id);
    }

    public List<Facxrecauda> getByUsuFecha(Long idusuario, Date d, Date h) {
        return dao.getByUsuFecha(idusuario, d, h);
    }

    public Facxrecauda getByIdFactura(Long idfactura) {
        return dao.getyByIdFactura(idfactura);
    }
}
