package com.erp.login.services;

import com.erp.login.models.Ventanas;
import com.erp.login.repositories.VentanasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VentanasService {
    @Autowired
    VentanasR dao;

    public Ventanas findVentana(Long idusuario, String nombre) {
        return dao.findByIdusuarioAndNombre(idusuario, nombre);
    }

    public <S extends Ventanas> S save(S x) {
        return dao.save( x );
    }

    public Optional<Ventanas> findById(Long id) {
        return dao.findById(id);
    }
}
