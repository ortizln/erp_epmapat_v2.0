package com.erp.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.modelo.Impuestos;
import com.erp.servicio.ImpuestosServicio;

@RestController
@RequestMapping("/impuestos")

public class ImpuestosApi {
    @Autowired
    private ImpuestosServicio impuestosServicio;

    @GetMapping("/currently")
    public ResponseEntity<Impuestos> getCurrentImpuesto() {
        return ResponseEntity.ok(impuestosServicio.getCurrentImpuesto());

    }
}
