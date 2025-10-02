package com.erp.login.controllers;

import com.erp.login.models.Acceso;
import com.erp.login.services.AccesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/acceso")
@CrossOrigin("*")
public class AccesoApi {
    @Autowired
    AccesoService accServicio;

    @GetMapping
    public List<Acceso> getAll() {
        return accServicio.findAll();
    }
}
