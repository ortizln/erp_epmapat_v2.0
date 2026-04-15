package com.erp.rrhh.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.rrhh.modelo.Contemergencia;
import com.erp.rrhh.servicio.ContemergenciaServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/contemergencia")

public class ContemergenciasApi {
    @Autowired
    private ContemergenciaServicio contemergenciaServicio;

    @GetMapping
    public ResponseEntity<List<Contemergencia>> getAll() {
        return ResponseEntity.ok(contemergenciaServicio.findAll());
    }

    @GetMapping("/bynombre")
    public ResponseEntity<List<Contemergencia>> getByContEmergencia(@RequestParam String nombre) {
        return ResponseEntity.ok(contemergenciaServicio.findByContEmergencia(nombre.toLowerCase()));
    }

    @PostMapping
    public ResponseEntity<Contemergencia> save(@RequestBody Contemergencia contemergencia) {
        return ResponseEntity.ok(contemergenciaServicio.save(contemergencia));
    }
}


