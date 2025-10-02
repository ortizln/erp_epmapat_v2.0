package com.erp.pagosonline.services;

import com.erp.pagosonline.models.Gdcajas;
import com.erp.pagosonline.repositories.GdcajasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GdcajasService {
    @Autowired
    private GdcajasR dao;

    public List<Gdcajas> findAll(){
        return dao.findAll();
    }
    public Optional<Gdcajas> findById(Long id){
        return dao.findById(id);
    }
    public Gdcajas save(Gdcajas caja){
        return dao.save(caja);
    }
}
