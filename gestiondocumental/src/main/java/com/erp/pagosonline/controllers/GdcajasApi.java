package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Gdcajas;
import com.erp.pagosonline.services.GdcajasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/gdcajas")
@CrossOrigin("*")
public class GdcajasApi {
    @Autowired
    private GdcajasService gdcajasService;

    @RequestMapping
    public ResponseEntity<List<Gdcajas>> getAll(){
        List<Gdcajas> gdcajas = gdcajasService.findAll();
        if(gdcajas != null){
            return ResponseEntity.ok(gdcajas);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idgdcaja}")
    public ResponseEntity<Optional<Gdcajas>> getById(@PathVariable Long idgdcaja){
        Optional<Gdcajas> gdcajas = gdcajasService.findById(idgdcaja);
        if(gdcajas.isPresent()){
            return ResponseEntity.ok(gdcajas);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Gdcajas> saveGd(@RequestBody Gdcajas cd){
        if(cd != null){
            return ResponseEntity.ok(gdcajasService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
}
