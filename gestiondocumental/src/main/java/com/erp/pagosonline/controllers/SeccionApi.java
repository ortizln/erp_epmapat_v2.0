package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Seccion;
import com.erp.pagosonline.services.SeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/seccion")
@CrossOrigin("*")
public class SeccionApi {
    @Autowired
    private SeccionService seccionService;
    @RequestMapping
    public ResponseEntity<List<Seccion>> getAll(){
        List<Seccion> seccion = seccionService.findAll();
        if(seccion != null){
            return ResponseEntity.ok(seccion);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idseccion}")
    public ResponseEntity<Optional<Seccion>> getById(@PathVariable Long idseccion){
        Optional<Seccion> seccion = seccionService.findById(idseccion);
        if(seccion.isPresent()){
            return ResponseEntity.ok(seccion);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Seccion> saveGd(@RequestBody Seccion cd){
        if(cd != null){
            return ResponseEntity.ok(seccionService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
}
