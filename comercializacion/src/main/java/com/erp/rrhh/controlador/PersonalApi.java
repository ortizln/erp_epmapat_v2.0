package com.erp.rrhh.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.rrhh.modelo.Personal;
import com.erp.rrhh.servicio.PersonalServicio;

@RestController
@RequestMapping("/api/personal")

public class PersonalApi {
    @Autowired
    private PersonalServicio personalServicio;

    @GetMapping
    public ResponseEntity<List<Personal>> getAll() {
        return ResponseEntity.ok(personalServicio.findAll());
    }

    @PostMapping
    public ResponseEntity<Personal> save(@RequestBody Personal p) {
        return ResponseEntity.ok(personalServicio.save(p));
    }
}

