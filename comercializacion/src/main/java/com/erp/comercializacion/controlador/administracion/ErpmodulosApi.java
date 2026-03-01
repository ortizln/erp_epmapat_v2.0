package com.erp.comercializacion.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.comercializacion.modelo.administracion.Erpmodulos;
import com.erp.comercializacion.servicio.administracion.ErpmodulosServicio;

@RestController
@RequestMapping("/api/erpmodulos")

public class ErpmodulosApi {
    @Autowired
    private ErpmodulosServicio emServicio;

    @GetMapping
    public ResponseEntity<List<Erpmodulos>> getAll() {
        return ResponseEntity.ok(emServicio.findAll());
    }
}

