package com.erp.controlador.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.modelo.rrhh.Contemergencia;
import com.erp.servicio.rrhh.ContemergenciaServicio;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/contemergencia")

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
