package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Conservacion_documental;
import com.erp.pagosonline.services.Conservacion_documentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/conservacion_documental")
@CrossOrigin("*")
public class Conservacion_documentalApi {
    @Autowired
    private Conservacion_documentalService conservacionDocumentalService;
    @RequestMapping
    public ResponseEntity<List<Conservacion_documental>> getAll(){
        List<Conservacion_documental> conservacionDocumental = conservacionDocumentalService.findAll();
        if(conservacionDocumental != null){
            return ResponseEntity.ok(conservacionDocumental);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idconservaciondocumental}")
    public ResponseEntity<Optional<Conservacion_documental>> getById(@PathVariable Long idconservaciondocumental){
        Optional<Conservacion_documental> conservacionDocumental = conservacionDocumentalService.findById(idconservaciondocumental);
        if(conservacionDocumental.isPresent()){
            return ResponseEntity.ok(conservacionDocumental);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Conservacion_documental> saveGd(@RequestBody Conservacion_documental cd){
        if(cd != null){
            return ResponseEntity.ok(conservacionDocumentalService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }

}
