package com.erp.comercializacion.services;

import com.erp.comercializacion.repositories.AnulpagoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnulpagoService {
    @Autowired
    private AnulpagoR dao;
}
