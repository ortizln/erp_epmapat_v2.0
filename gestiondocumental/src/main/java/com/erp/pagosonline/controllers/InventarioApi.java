package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Inventario;
import com.erp.pagosonline.services.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/inventario")
@CrossOrigin("*")
public class InventarioApi {
    @Autowired
    private InventarioService inventarioService;

    @RequestMapping
    public ResponseEntity<List<Inventario>> getAll(){
        List<Inventario> inventario = inventarioService.findAll();
        if(inventario != null){
            return ResponseEntity.ok(inventario);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idinventario}")
    public ResponseEntity<Optional<Inventario>> getById(@PathVariable Long idinventario){
        Optional<Inventario> inventario = inventarioService.findById(idinventario);
        if(inventario.isPresent()){
            return ResponseEntity.ok(inventario);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Inventario> saveGd(@RequestBody Inventario cd){
        if(cd != null){
            return ResponseEntity.ok(inventarioService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
}
