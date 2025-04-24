package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.GdcajasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gdcajas")
@CrossOrigin("*")
public class GdcajasApi {
    @Autowired
    private GdcajasService gdcajasService;
}
