package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Condmultasintereses;
import com.erp.comercializacion.services.CondmultasinteresesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/condmultasintereses")
@CrossOrigin("*")
public class CondMultasInteresesApi {
    @Autowired
   private CondmultasinteresesService codservice;

    @PostMapping
    public ResponseEntity<Condmultasintereses> SaveCondonacion(@RequestBody Condmultasintereses entity) {
        return ResponseEntity.ok(codservice.save(entity));
    }

}
