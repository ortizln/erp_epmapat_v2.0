package com.erp.sri.controller;

import com.erp.sri.service.Interes_ser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rec/interes")
@CrossOrigin("*")
public class Interes_api {
    @Autowired
    private Interes_ser s_interes;
    @GetMapping
    public ResponseEntity<Object> findInteres(Long idfactura){
        return ResponseEntity.ok(s_interes.interesOfFactura(idfactura));
    }
}
