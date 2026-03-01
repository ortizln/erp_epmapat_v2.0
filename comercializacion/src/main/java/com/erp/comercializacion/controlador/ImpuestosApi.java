package com.erp.comercializacion.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.comercializacion.modelo.Impuestos;
import com.erp.comercializacion.servicio.ImpuestosServicio;

@RestController
@RequestMapping("/api/impuestos")

public class ImpuestosApi {
    @Autowired
    private ImpuestosServicio impuestosServicio;

    @GetMapping("/currently")
    public ResponseEntity<Impuestos> getCurrentImpuesto() {
        return ResponseEntity.ok(impuestosServicio.getCurrentImpuesto());

    }
}


