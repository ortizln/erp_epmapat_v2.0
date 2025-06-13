package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Novedades;
import com.erp.comercializacion.services.NovedadesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/novedades")
@CrossOrigin(origins = "*")

public class NovedadesApi {

    @Autowired
    NovedadesService novServicio;

    // @GetMapping
    // public List<Novedad> getAllNovedades() {
    // return novServicio.findAll();
    // }

    @GetMapping
    public List<Novedades> getAll(@Param(value = "descripcion") String descripcion) {
        if (descripcion != null) {
            return novServicio.findByDescri(descripcion);
        } else {
            return novServicio.findAll();
        }
    }
    @GetMapping("/estado")
    public ResponseEntity<List<Novedades>> geyByEstado(@RequestParam("estado") Long estado){
    	return ResponseEntity.ok(novServicio.findByEstado(estado));
    }

    @PostMapping
    public Novedades updateOrSave(@RequestBody Novedades nov) {
        return novServicio.save(nov);
    }

    @GetMapping("/{idnovedad}")
    public ResponseEntity<Novedades> getByIdNovedad(@PathVariable Long idnovedad) {
        Novedades x = novServicio.findById(idnovedad)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Novedad Id: " + idnovedad)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idnovedad}")
    public ResponseEntity<Novedades> updateNovedad(@PathVariable Long idnovedad, @RequestBody Novedades x) {
        Novedades y = novServicio.findById(idnovedad)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Novedad Id: " + idnovedad)));
        y.setDescripcion(x.getDescripcion());
        y.setEstado(x.getEstado());
        y.setUsucrea(x.getUsucrea());
        // y.setFeccrea(x.getFeccrea());

        Novedades xy = novServicio.save(y);
        return ResponseEntity.ok(xy);
    }

    @PatchMapping("/{idnovedad}")
    public ResponseEntity<Novedades> updatePartially(@PathVariable Long idnovedad, @RequestBody Novedades x) {
        Novedades y = novServicio.findById(idnovedad)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(("No existe la Novedad Id: " + idnovedad)));
        y.setDescripcion(x.getDescripcion());
        if (y.getEstado() != x.getEstado()) {
            y.setEstado(x.getEstado());
        }

        Novedades xy = novServicio.save(y);
        return ResponseEntity.ok(xy);
    }

    @DeleteMapping("/{idnovedad}")
    private ResponseEntity<Boolean> deleteNovedad(@PathVariable("idnovedad") Long idnovedad) {
        novServicio.deleteById(idnovedad);
        return ResponseEntity.ok(!(novServicio.findById(idnovedad) != null));
    }

}