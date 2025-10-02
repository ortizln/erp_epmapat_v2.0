package com.erp.sri.controller;

import com.erp.sri.service.Impuestos_ser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/rec/impuestos")
@CrossOrigin("*")
public class Impuestos_api {
    @Autowired
    private Impuestos_ser s_impuetos;
    @GetMapping
    ResponseEntity<BigDecimal> findImpuesto(@RequestParam Long idfactura){
        return ResponseEntity.ok(s_impuetos.calcularIva(idfactura));
    }
}
