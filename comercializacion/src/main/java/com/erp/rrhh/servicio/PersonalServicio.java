package com.erp.rrhh.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.rrhh.modelo.Personal;
import com.erp.rrhh.repositorio.PersonalR;

@Service
public class PersonalServicio {
    @Autowired
    private PersonalR dao;

    public List<Personal> findAll() {
        return dao.findAll();
    }

    public Personal save(Personal p) {
        return dao.save(p);
    }
}

