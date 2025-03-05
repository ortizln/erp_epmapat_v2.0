package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Estadom;
import com.erp.comercializacion.services.EstadomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estadom")
@CrossOrigin("*")
public class EstadomApi {
    @Autowired
    EstadomService EstmServicio;

    @GetMapping
    public List<Estadom> getAllEstadoms() {
        return EstmServicio.findAll();
    }

    @PostMapping
    public Estadom updateOrSave(@RequestBody Estadom x) {
        return EstmServicio.save(x);
    }

    @GetMapping("/{idestadom}")
    public ResponseEntity<Estadom> getById(@PathVariable Long idestadom) {
        Estadom x = EstmServicio.findById(idestadom)
                .orElseThrow(null);
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idestadom}")
    public ResponseEntity<Estadom> update(@PathVariable Long idestadom, @RequestBody Estadom x) {
        Estadom y = EstmServicio.findById(idestadom)
                .orElseThrow(null);
        y.setDescripcion(x.getDescripcion());
        y.setUsucrea(x.getUsucrea());
        //    y.setFeccrea(x.getFeccrea());
        //    y.setUsumodi(x.getUsumodi());
        //    y.setFecmodi(x.getFecmodi());

        Estadom actualizar = EstmServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{idestadom}")
    private ResponseEntity<Boolean> delete(@PathVariable("idestadom") Long idestadom) {
        EstmServicio.deleteById(idestadom);
        return ResponseEntity.ok(!(EstmServicio.findById(idestadom) != null));
    }
}
