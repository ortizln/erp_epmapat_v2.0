package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Nacionalidad;
import com.erp.comercializacion.services.NacionalidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nacionalidades")
@CrossOrigin("*")
public class NacionalidadApi {
    @Autowired
    private NacionalidadService nacService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Nacionalidad> getAllNacionalidades(@Param(value = "idused") Long idused,
                                                   @Param(value = "descripcion") String descripcion) {
        if (descripcion != null) {
            return nacService.findByDescription(descripcion.toLowerCase());

        } else if (idused != null) {
            return nacService.used(idused);
        } else {
            return nacService.findAll(Sort.by(Sort.Order.asc("descripcion")));
        }
    }

    @PostMapping
    public Nacionalidad saveNacionalidad(@RequestBody Nacionalidad nacionalidadM) {
        return nacService.save(nacionalidadM);
    }

    @GetMapping("/{idnacionalidad}")
    public ResponseEntity<Nacionalidad> getByIdNacionalidad(@PathVariable Long idnacionalidad) {
        Nacionalidad nacionalidadM = nacService.findById(idnacionalidad)
                .orElseThrow(null);
        return ResponseEntity.ok(nacionalidadM);
    }

    @PutMapping(value = "/{idnacionalidad}")
    public ResponseEntity<Nacionalidad> updateNacionalidad(@PathVariable Long idnacionalidad,
                                                           @RequestBody Nacionalidad descripcion) {
        Nacionalidad nacionalidadM = nacService.findById(idnacionalidad)
                .orElseThrow(null);
        nacionalidadM.setDescripcion(descripcion.getDescripcion());
        Nacionalidad updaNacionalidad = nacService.save(nacionalidadM);
        return ResponseEntity.ok(updaNacionalidad);
    }

    // @PutMapping(value = "/{idnacionalidad}")
    // public ResponseEntity<NacionalidadM> update(@PathVariable Long
    // idnacionalidad, @RequestBody NacionalidadM x) {
    // NacionalidadM y = nacService.findById(idnacionalidad)
    // .orElseThrow(() -> new ResourceNotFoundExcepciones(
    // ("No existe Nacionalidad Id: " + idnacionalidad)));
    // y.setDescripcion(x.getDescripcion());

    // NacionalidadM actualizar = nacService.save(y);
    // return ResponseEntity.ok(actualizar);
    // }

    @DeleteMapping(value = "/{idnacionalidad}")
    private ResponseEntity<Boolean> deleteNacionalidad(@PathVariable("idnacionalidad") Long idnacionalidad) {
        nacService.deleteById(idnacionalidad);
        return ResponseEntity.ok(!(nacService.findById(idnacionalidad) != null));

    }

}
