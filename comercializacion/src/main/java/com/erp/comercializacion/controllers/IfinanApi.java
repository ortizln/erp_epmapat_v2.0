package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Ifinan;
import com.erp.comercializacion.services.IfinanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ifinan")
@CrossOrigin("*")

public class IfinanApi {

    @Autowired
    private IfinanService ifinanServicio;

    @GetMapping
    public List<Ifinan> getIfinan(@Param(value = "codifinan") String codifinan,
            @Param(value = "nomifinan") String nomifinan) {
        if (codifinan != null) {
            return ifinanServicio.findByCodifinan(codifinan);
        }
        if (nomifinan != null) {
            return ifinanServicio.findByNomifinan(nomifinan.toLowerCase());
        } else return null;
    }

    @PostMapping
    public Ifinan updateOrSave(@RequestBody Ifinan x) {
        return ifinanServicio.save(x);
    }

    @GetMapping("/{idifinan}")
    public ResponseEntity<Ifinan> getByIdNovedad(@PathVariable Long idifinan) {
        Ifinan x = ifinanServicio.findById(idifinan)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Institución con Id: " + idifinan)));
        return ResponseEntity.ok(x);
    }

    @PutMapping("/{idifinan}")
    public ResponseEntity<Ifinan> update(@PathVariable Long idifinan, @RequestBody Ifinan x) {
        Ifinan y = ifinanServicio.findById(idifinan)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        ("No existe Institución con Id: " + idifinan)));
        // y.setCodifinan(x.getCodifinan());
        // y.setNomifinan(x.getNomifinan());
        // y.setFoto(x.getFoto());

        Ifinan actualizar = ifinanServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    // @DeleteMapping("/{idifinan}")
    // private ResponseEntity<Boolean> deleteIfinan(@PathVariable("idifinan") Long
    // idifinan) {
    // ifinanServicio.deleteById(idifinan);
    // return ResponseEntity.ok(!(ifinanServicio.findById(idifinan) != null));
    // }

}
