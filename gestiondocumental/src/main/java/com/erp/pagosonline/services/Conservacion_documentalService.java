package com.erp.pagosonline.services;

import com.erp.pagosonline.models.Conservacion_documental;
import com.erp.pagosonline.repositories.Conservacion_documentalR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Conservacion_documentalService {
    @Autowired
    private Conservacion_documentalR dao;
    public List<Conservacion_documental> findAll(){
        return dao.findAll();
    }
    public Optional<Conservacion_documental> findById(Long id){
        return dao.findById(id);
    }
    public Conservacion_documental save(Conservacion_documental cd){
        return dao.save(cd);
    }
}
