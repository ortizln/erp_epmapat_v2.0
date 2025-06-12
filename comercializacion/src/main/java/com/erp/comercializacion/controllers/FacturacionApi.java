package com.erp.comercializacion.controllers;

import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Facturacion;
import com.erp.comercializacion.services.FacturacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

@RestController
@RequestMapping("/facturacion")
@CrossOrigin("*")

public class FacturacionApi {

   @Autowired
   private FacturacionService factuServicio;

   @GetMapping
   public List<Facturacion> getFacturacion(@Param(value = "desde") Long desde, @Param(value = "hasta") Long hasta,
                                           @Param(value = "del") @DateTimeFormat(pattern = "yyyy-MM-dd") Date del,
                                           @Param(value = "al") @DateTimeFormat(pattern = "yyyy-MM-dd") Date al,
                                           @Param(value = "cliente") String cliente) {
      if (cliente != null) {
         return factuServicio.findByCliente(cliente.toLowerCase(), del, al);
      } else
         return factuServicio.findDesdeHasta(desde, hasta, del, al);
   }

   @GetMapping("/ultimo")
   public Facturacion ultimo() {
      return factuServicio.ultimo();
   }

   @GetMapping("/{idfacturacion}")
   public ResponseEntity<Facturacion> getById(@PathVariable Long idfacturacion) {
      Facturacion x = factuServicio.findById(idfacturacion)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Facturacion Id: " + idfacturacion)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public ResponseEntity<Facturacion> save(@RequestBody Facturacion facturacion) {
      return ResponseEntity.ok(factuServicio.save(facturacion));
   }

   @DeleteMapping(value = "/{idfacturacion}")
   private ResponseEntity<Boolean> delete(@PathVariable("idfacturacion") Long idfacturacion) {
      factuServicio.deleteById(idfacturacion);
      return ResponseEntity.ok(!(factuServicio.findById(idfacturacion) != null));
   }
}
