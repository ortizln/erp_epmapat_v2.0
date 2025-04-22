package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Facelectro;
import com.erp.comercializacion.services.FacelectroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/facelectro")
@CrossOrigin("*")
public class FacelectroApi {

    @Autowired
    private FacelectroService faceleServicio;

    @GetMapping
    public List<Facelectro> get(@RequestParam(value = "idcliente") Long idcliente) {
        return faceleServicio.findByIdcliente(idcliente);
    }

    // Solo para probar en Postman
    // @GetMapping
    // public List<Facelectro> get20() {
    // return faceleServicio.find20();
    // }

    // facelectro.idfacelectro no es clave foranea de ningún Tabla (Se usa solo para
    // probar en Postman )
    @GetMapping("/{idfacelectro}")
    public ResponseEntity<Facelectro> getById(@PathVariable Long idfacelectro) {
        Facelectro x = faceleServicio.findById(idfacelectro)
                .orElseThrow(null);
        return ResponseEntity.ok(x);
    }
}
