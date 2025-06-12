package com.erp.comercializacion.controllers;

import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Facxconvenio;
import com.erp.comercializacion.services.FacxconvenioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facxconvenio")
@CrossOrigin("*")

public class FacxconvenioApi {

   @Autowired
   private FacxconvenioService fxconvServicio;

   @GetMapping()
   public List<Facxconvenio> findByConvenio(@Param(value = "idconvenio") Long idconvenio) {
      if (idconvenio != null) {
         return fxconvServicio.findByConvenio(idconvenio);
      } else {
         return fxconvServicio.find10();
      }
   }

   @GetMapping("/{idfacxconvenio}")
   public ResponseEntity<Facxconvenio> getById(@PathVariable Long idfacxconvenio) {
      Facxconvenio x = fxconvServicio.findById(idfacxconvenio)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Factura  Id: " + idfacxconvenio)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Facxconvenio saveFacxconvenio(@RequestBody Facxconvenio x) {
      return (fxconvServicio.save( x ));
   }

}
