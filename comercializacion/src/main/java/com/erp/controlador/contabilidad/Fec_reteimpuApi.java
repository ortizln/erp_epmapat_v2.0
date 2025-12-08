package com.erp.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.erp.modelo.contabilidad.Fec_reteimpu;
import com.erp.servicio.contabilidad.Fec_reteimpuServicio;

@RestController
@RequestMapping("/api/fec_reteimpu")


public class Fec_reteimpuApi {

   @Autowired
   private Fec_reteimpuServicio fec_reteimpuServicio;

   @GetMapping
   public List<Fec_reteimpu> getAll() {
      return fec_reteimpuServicio.findAll();
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Fec_reteimpu saveFec_reteimpu(@RequestBody Fec_reteimpu x) {
      return fec_reteimpuServicio.save(x);
   }

}
