package com.erp.pagosonline.services;

import com.erp.pagosonline.repositories.SeccionR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {
    @Autowired
    private SeccionR dao;
}
