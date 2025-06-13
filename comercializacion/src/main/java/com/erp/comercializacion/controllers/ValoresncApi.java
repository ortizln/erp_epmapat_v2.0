package com.erp.comercializacion.controllers;
import java.util.List;
import java.util.Optional;

import com.erp.comercializacion.models.Valoresnc;
import com.erp.comercializacion.services.ValoresncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/valoresnc")
@CrossOrigin("*")
public class ValoresncApi {
    @Autowired
    private ValoresncService valoresServicio;
    
    
    @GetMapping
    public ResponseEntity<List<Valoresnc>> getAll() {
        return ResponseEntity.ok(valoresServicio.findAll());
    }

    @GetMapping("/{idvaloresnc}")
    public ResponseEntity<Optional<Valoresnc>> getById(@PathVariable Long idvaloresnc) {
        return ResponseEntity.ok(valoresServicio.findById(idvaloresnc));
    }

    @PostMapping
    public ResponseEntity<Valoresnc> save(@RequestBody Valoresnc valoresnc) {
        return ResponseEntity.ok(valoresServicio.save(valoresnc));
    }
}
