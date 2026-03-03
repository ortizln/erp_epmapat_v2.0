package com.erp.gestiondocumental.services;

import com.erp.gestiondocumental.models.Seccion;
import com.erp.gestiondocumental.repositories.SeccionR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeccionService {
    @Autowired
    private SeccionR dao;
    public List<Seccion> findAll(){
        return dao.findAll();
    }
    public Optional<Seccion> findById(Long id){
        return dao.findById(id);
    }
    public Seccion save(Seccion seccion){
        return dao.save(seccion);
    }
}

