package com.erp.login.controllers;

import com.erp.login.models.Erpmodulos;
import com.erp.login.services.ErpmodulosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/erpmodulos")
public class ErpmodulosApi {
    @Autowired
    private ErpmodulosService emServicio;

    @GetMapping
    public ResponseEntity<List<Erpmodulos>> getAll() {
        return ResponseEntity.ok(emServicio.findAll());
    }

    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<Erpmodulos>> findByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(emServicio.findByPlatform(platform));
    }

    @PostMapping
    public ResponseEntity<Erpmodulos> save(@RequestBody Erpmodulos modulo) {
        if (modulo.getPlatform() == null || modulo.getPlatform().isBlank()) {
            modulo.setPlatform("WEB");
        }
        modulo.setPlatform(modulo.getPlatform().trim().toUpperCase());
        return ResponseEntity.ok(emServicio.save(modulo));
    }
}
