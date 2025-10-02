package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Subseries;
import com.erp.pagosonline.services.SubseriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/subseries")
@CrossOrigin("*")
public class SubseriesApi {
    @Autowired
    private SubseriesService subseriesService;
    @RequestMapping
    public ResponseEntity<List<Subseries>> getAll(){
        List<Subseries> subseries = subseriesService.findAll();
        if(subseries != null){
            return ResponseEntity.ok(subseries);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idsubseries}")
    public ResponseEntity<Optional<Subseries>> getById(@PathVariable Long idsubseries){
        Optional<Subseries> subseries = subseriesService.findById(idsubseries);
        if(subseries.isPresent()){
            return ResponseEntity.ok(subseries);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Subseries> saveGd(@RequestBody Subseries cd){
        if(cd != null){
            return ResponseEntity.ok(subseriesService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
}
