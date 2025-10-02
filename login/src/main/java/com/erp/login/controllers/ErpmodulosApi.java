package com.erp.login.controllers;

import com.erp.login.models.Erpmodulos;
import com.erp.login.services.ErpmodulosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/erpmodulos")
@CrossOrigin("*")
public class ErpmodulosApi {
    @Autowired
    private ErpmodulosService emServicio;

    @GetMapping
    public ResponseEntity<List<Erpmodulos>> getAll() {
        return ResponseEntity.ok(emServicio.findAll());
    }
}
