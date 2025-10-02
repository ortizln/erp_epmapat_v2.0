package com.erp.pagosonline.controllers;

import com.erp.pagosonline.models.Series;
import com.erp.pagosonline.services.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/series")
@CrossOrigin("*")
public class SeriesApi {
    @Autowired
    private SeriesService seriesService;
    @RequestMapping
    public ResponseEntity<List<Series>> getAll(){
        List<Series> Series = seriesService.findAll();
        if(Series != null){
            return ResponseEntity.ok(Series);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @RequestMapping("/{idseries}")
    public ResponseEntity<Optional<Series>> getById(@PathVariable Long idseries){
        Optional<Series> Series = seriesService.findById(idseries);
        if(Series.isPresent()){
            return ResponseEntity.ok(Series);
        }
        else {
            return ResponseEntity.noContent().build();
        }
    }
    @PostMapping
    public ResponseEntity<Series> saveGd(@RequestBody Series cd){
        if(cd != null){
            return ResponseEntity.ok(seriesService.save(cd));
        }
        else{
            return ResponseEntity.noContent().build();
        }
    }
}
