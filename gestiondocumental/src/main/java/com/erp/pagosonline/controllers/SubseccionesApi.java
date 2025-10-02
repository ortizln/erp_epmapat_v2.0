package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Subsecciones;
import com.erp.pagosonline.services.SubseccionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subsecciones")
@CrossOrigin("*")
public class SubseccionesApi {
    @Autowired
    private SubseccionesService subseccionesService;
    @RequestMapping
    public ResponseEntity<List<Subsecciones>> getAll(){
        List<Subsecciones> seccion = subseccionesService.findAll();
        if(seccion != null){
            return ResponseEntity.ok(seccion);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idsubseccion}")
    public ResponseEntity<Optional<Subsecciones>> getById(@PathVariable Long idseccion){
        Optional<Subsecciones> seccion = subseccionesService.findById(idseccion);
        if(seccion.isPresent()){
            return ResponseEntity.ok(seccion);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Subsecciones> saveGd(@RequestBody Subsecciones cd){
        if(cd != null){
            return ResponseEntity.ok(subseccionesService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
}
