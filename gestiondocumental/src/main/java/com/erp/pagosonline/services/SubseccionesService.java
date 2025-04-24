package com.erp.pagosonline.services;

import com.erp.pagosonline.models.Subsecciones;
import com.erp.pagosonline.models.Subsecciones;
import com.erp.pagosonline.repositories.SubseccionesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubseccionesService {
    @Autowired
    private SubseccionesR dao;
    public List<Subsecciones> findAll(){
        return dao.findAll();
    }
    public Optional<Subsecciones> findById(Long id){
        return dao.findById(id);
    }
    public Subsecciones save(Subsecciones subsecciones){
        return dao.save(subsecciones);
    }
}
