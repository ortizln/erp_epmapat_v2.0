package com.erp.pagosonline.services;

import com.erp.pagosonline.repositories.InventarioR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InventarioService {
    @Autowired
    private InventarioR dao;
}
