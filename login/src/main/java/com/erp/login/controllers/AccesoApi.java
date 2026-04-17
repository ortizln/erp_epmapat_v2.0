package com.erp.login.controllers;

import com.erp.login.config.AESUtil;
import com.erp.login.models.Acceso;
import com.erp.login.services.AccesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acceso")

public class AccesoApi {
    @Autowired
    AccesoService accServicio;

    @GetMapping
    public List<Acceso> getAll() {
        return accServicio.findAll();
    }


}
