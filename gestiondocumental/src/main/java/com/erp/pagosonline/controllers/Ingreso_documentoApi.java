package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.Ingreso_documentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingreso_documento")
@CrossOrigin("*")
public class Ingreso_documentoApi {
    @Autowired
    private Ingreso_documentosService ingresoDocumentosService;
}
