package com.erp.pagosonline.services;

import com.erp.pagosonline.repositories.SubseriesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubseriesService {
    @Autowired
    private SubseriesR dao;
}
