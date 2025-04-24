package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.SeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seccion")
@CrossOrigin("*")
public class SeccionApi {
    @Autowired
    private SeccionService seccionService;
}
