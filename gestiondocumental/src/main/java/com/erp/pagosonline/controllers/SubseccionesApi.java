package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.SubseccionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/subsecciones")
@CrossOrigin("*")
public class SubseccionesApi {
    @Autowired
    private SubseccionesService subseccionesService;
}
