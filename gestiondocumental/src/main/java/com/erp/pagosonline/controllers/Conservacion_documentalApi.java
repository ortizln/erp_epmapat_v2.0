package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.Conservacion_documentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conservacion_documental")
@CrossOrigin("*")
public class Conservacion_documentalApi {
    @Autowired
    private Conservacion_documentalService conservacionDocumentalService;
}
