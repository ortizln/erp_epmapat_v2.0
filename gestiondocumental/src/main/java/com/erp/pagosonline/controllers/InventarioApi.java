package com.erp.pagosonline.controllers;

import com.erp.pagosonline.services.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventario")
@CrossOrigin("*")
public class InventarioApi {
    @Autowired
    private InventarioService inventarioService;
}
