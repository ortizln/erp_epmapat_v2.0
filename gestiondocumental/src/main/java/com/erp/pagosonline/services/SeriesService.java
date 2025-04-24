package com.erp.pagosonline.services;

import com.erp.pagosonline.repositories.SeriesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeriesService {
    @Autowired
    private SeriesR dao;
}
