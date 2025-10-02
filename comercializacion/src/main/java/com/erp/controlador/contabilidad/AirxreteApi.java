package com.erp.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.modelo.contabilidad.Airxrete;
import com.erp.servicio.contabilidad.AirxreteServicio;

@RestController
@RequestMapping("/airxrete")
@CrossOrigin("*")

public class AirxreteApi {

   @Autowired
   private AirxreteServicio airxreteServicio;

   // @GetMapping
   // public ResponseEntity<List<Airxrete>> getAll() {
   // return ResponseEntity.ok(airxreteServicio.findAll());
   // }

   @GetMapping("/retencion")
   public ResponseEntity<List<Airxrete>> getByIdrete(@Param("idrete") Long idrete) {
      return ResponseEntity.ok(airxreteServicio.getByIdrete(idrete));
   }

   @PostMapping
   public Airxrete saveAirxrete(@RequestBody Airxrete airxrete) {
      return airxreteServicio.save(airxrete);
   }

}
