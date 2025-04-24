package com.erp.pagosonline.services;

import com.erp.pagosonline.repositories.SubseccionesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubseccionesService {
    @Autowired
    private SubseccionesR dao;
}
