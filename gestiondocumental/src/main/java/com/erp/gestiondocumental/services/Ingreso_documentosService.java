package com.erp.gestiondocumental.services;

import com.erp.gestiondocumental.models.Ingreso_documentos;
import com.erp.gestiondocumental.repositories.Ingreso_documentosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Ingreso_documentosService {
    @Autowired
    private Ingreso_documentosR dao;
    public List<Ingreso_documentos> findAll(){
        return dao.findAll();
    }
    public Optional<Ingreso_documentos> findById(Long id){
        return dao.findById(id);
    }
    public Ingreso_documentos save(Ingreso_documentos id){
        return dao.save(id);
    }
}

