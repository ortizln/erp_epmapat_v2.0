package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Ingreso_documentos;
import com.erp.pagosonline.services.Ingreso_documentosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingreso_documento")
@CrossOrigin("*")
public class Ingreso_documentoApi {
    @Autowired
    private Ingreso_documentosService ingresoDocumentosService;
    @RequestMapping
    public ResponseEntity<List<Ingreso_documentos>> getAll(){
        List<Ingreso_documentos> ingresodocumentos = ingresoDocumentosService.findAll();
        if(ingresodocumentos != null){
            return ResponseEntity.ok(ingresodocumentos);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idingresodocumentos}")
    public ResponseEntity<Optional<Ingreso_documentos>> getById(@PathVariable Long idingresodocumentos){
        Optional<Ingreso_documentos> ingresodocumentos = ingresoDocumentosService.findById(idingresodocumentos);
        if(ingresodocumentos.isPresent()){
            return ResponseEntity.ok(ingresodocumentos);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Ingreso_documentos> saveGd(@RequestBody Ingreso_documentos cd){
        if(cd != null){
            return ResponseEntity.ok(ingresoDocumentosService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
}
