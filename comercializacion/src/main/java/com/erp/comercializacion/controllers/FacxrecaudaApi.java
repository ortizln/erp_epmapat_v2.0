package com.erp.comercializacion.controllers;

import java.util.Date;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Facxrecauda;
import com.erp.comercializacion.services.FacxrecaudaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facxrecauda")
@CrossOrigin(origins = "*")

public class FacxrecaudaApi {

   @Autowired
   private FacxrecaudaService facxrServicio;

   @GetMapping("/{idfacxrecauda}")
   public ResponseEntity<Facxrecauda> getById(@PathVariable Long idfacxrecauda) {
      Facxrecauda x = facxrServicio.findById(idfacxrecauda)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No extiste la Factura por Recaudacion ID: " + idfacxrecauda)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Facxrecauda save(@RequestBody Facxrecauda x) {
      return facxrServicio.save(x);
   }

   @GetMapping("/reportes/usuario")
   public List<Facxrecauda> getByUsuFecha(@RequestParam("idusuario") Long idusuario,
         @RequestParam("d") @DateTimeFormat(pattern = "yyyy-MM-dd") Date d,
         @RequestParam("h") @DateTimeFormat(pattern = "yyyy-MM-dd") Date h) {
      return facxrServicio.getByUsuFecha(idusuario, d, h);
   }

   @GetMapping("/factura")
   public ResponseEntity<Facxrecauda> getyByIdFactura(@RequestParam("idfactura") Long idfactura) {
      Facxrecauda facxrecauda = facxrServicio.getByIdFactura(idfactura);
      if (facxrecauda != null) {
         return ResponseEntity.ok(facxrecauda);
      } else {
         return ResponseEntity.noContent().build();
      }
   }

}