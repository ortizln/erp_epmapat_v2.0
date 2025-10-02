package com.erp.login.controllers;

import com.erp.login.models.Definir;
import com.erp.login.services.DefinirService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/definir")
@CrossOrigin("*")
public class DefinirApi {
    @Autowired
    DefinirService defServicio;

    @GetMapping("/{iddefinir}")
    public ResponseEntity<Definir> getByIddefinir(@PathVariable Long iddefinir) {
        Definir definir = defServicio.findById(iddefinir).orElse(null);
        return definir != null ? ResponseEntity.ok(definir) : ResponseEntity.noContent().build();
    }

    @PutMapping("/{iddefinir}")
    public ResponseEntity<Definir> update(@PathVariable Long iddefinir, @RequestBody Definir x) {
        Definir y = defServicio.findById(iddefinir)
                .orElseThrow(null);
        y.setRazonsocial(x.getRazonsocial());
        y.setNombrecomercial(x.getNombrecomercial());
        y.setRuc(x.getRuc());
        y.setDireccion(x.getDireccion());
        y.setTipoambiente(x.getTipoambiente());
        y.setIva(x.getIva());

        Definir z = defServicio.save(y);
        return ResponseEntity.ok(z);
    }
}
