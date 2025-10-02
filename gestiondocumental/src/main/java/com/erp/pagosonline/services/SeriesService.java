package com.erp.pagosonline.services;

import com.erp.pagosonline.models.Series;
import com.erp.pagosonline.repositories.SeriesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeriesService {
    @Autowired
    private SeriesR dao;
    public List<Series> findAll(){
        return dao.findAll();
    }
    public Optional<Series> findById(Long id){
        return dao.findById(id);
    }
    public Series save(Series series){
        return dao.save(series);
    }
}
