package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.Rutas;
import com.erp.comercializacion.services.RutasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rutas")
@CrossOrigin("*")
public class RutasApi {
    @Autowired
    private RutasService rutServicio;

    @GetMapping
    public List<Rutas> getAll() {
        return rutServicio.findAllActive();
    }

    @GetMapping("/valCodigo")
    public ResponseEntity<Boolean> valCodigo(@Param(value = "codigo") String codigo) {
        boolean rtn = rutServicio.valCodigo( codigo );
        return ResponseEntity.ok(rtn);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Rutas saveRutas(@RequestBody Rutas x) {
        return rutServicio.save( x );
    }

    @GetMapping("/{idruta}")
    public ResponseEntity<Rutas> getByIdRutas(@PathVariable Long idruta){
        Rutas rutasM = rutServicio.findById(idruta)
                .orElseThrow(null);
        return ResponseEntity.ok(rutasM);
    }
    @PutMapping(value = "/{idruta}")
    public ResponseEntity<Rutas> updateRutas(@PathVariable Long idruta, @RequestBody Rutas rutasm){
        Rutas rutasM = rutServicio.findById(idruta)
                .orElseThrow(null);
        rutasM.setDescripcion(rutasm.getDescripcion());
        rutasM.setOrden(rutasm.getOrden());
        rutasM.setUsucrea(rutasm.getUsucrea());
        rutasM.setCodigo(rutasm.getCodigo());
        rutasM.setFeccrea(rutasm.getFeccrea());
        rutasM.setUsumodi(rutasm.getUsumodi());
        rutasM.setFecmodi(rutasm.getFecmodi());
        Rutas updateRutas = rutServicio.save(rutasM);
        return ResponseEntity.ok(updateRutas);
    }
    @DeleteMapping(value = "/{idruta}")
    public ResponseEntity<Boolean> delteRuta(@PathVariable("idruta") Long idruta){
        rutServicio.deleteById(idruta);
        return ResponseEntity.ok(!(rutServicio.findById(idruta)!=null));
    }
}
