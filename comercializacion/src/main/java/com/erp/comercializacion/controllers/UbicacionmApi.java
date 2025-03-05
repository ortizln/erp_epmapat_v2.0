package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Ubicacionm;
import com.erp.comercializacion.services.UbicacionmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ubicacionm")
@CrossOrigin("*")
public class UbicacionmApi {
    @Autowired
    private UbicacionmService ubimServicio;

    @GetMapping
    public List<Ubicacionm> getAllUbicacionm() {
        return ubimServicio.findAll();
    }

    @PostMapping
    public Ubicacionm updateOrSave(@RequestBody Ubicacionm x) {
        return ubimServicio.save(x);
    }

    @GetMapping("/{idubicacionm}")
    public ResponseEntity<Ubicacionm> getByIdUbicacionm(@PathVariable Long idubicacionm) {
        Ubicacionm x = ubimServicio.findById(idubicacionm)
                .orElseThrow(null);
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idubicacionm}")
    public ResponseEntity<Ubicacionm> update(@PathVariable Long idubicacionm, @RequestBody Ubicacionm x) {
        Ubicacionm y = ubimServicio.findById(idubicacionm)
                .orElseThrow(null);
        y.setDescripcion(x.getDescripcion());
        y.setUsucrea(x.getUsucrea());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());

        Ubicacionm actualizar = ubimServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @DeleteMapping("/{idubicacionm}")
    private ResponseEntity<Boolean> delete(@PathVariable("idubicacionm") Long idubicacionm) {
        ubimServicio.deleteById(idubicacionm);
        return ResponseEntity.ok(!(ubimServicio.findById(idubicacionm) != null));
    }
}
