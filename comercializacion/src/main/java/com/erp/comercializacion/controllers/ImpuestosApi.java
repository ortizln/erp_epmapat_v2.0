package com.erp.comercializacion.controllers;
import com.erp.comercializacion.models.Impuestos;
import com.erp.comercializacion.services.ImpuestosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/impuestos")
@CrossOrigin("*")
public class ImpuestosApi {
    @Autowired
    private ImpuestosService impuestosServicio;

    @GetMapping("/currently")
    public ResponseEntity<Impuestos> getCurrentImpuesto() {
        return ResponseEntity.ok(impuestosServicio.getCurrentImpuesto());

    }
}
