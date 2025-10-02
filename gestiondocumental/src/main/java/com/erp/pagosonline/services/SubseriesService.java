package com.erp.pagosonline.services;

import com.erp.pagosonline.models.Subseries;
import com.erp.pagosonline.repositories.SubseriesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubseriesService {
    @Autowired
    private SubseriesR dao;
    public List<Subseries> findAll(){
        return dao.findAll();
    }
    public Optional<Subseries> findById(Long id){
        return dao.findById(id);
    }
    public Subseries save(Subseries subseries){
        return dao.save(subseries);
    }
}
