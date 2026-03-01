package com.erp.contabilidad.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.contabilidad.modelo.Unicostos;
import com.erp.contabilidad.servicio.UnicostosServicio;

@RestController
@RequestMapping("/api/unicostos")


public class UnicostosApi {

   @Autowired
   UnicostosServicio unicostServicio;

   // Todas ordendas por mes (sin necesidad de select en el repositorio)
   @GetMapping
   public List<Unicostos> getAllIfinans() {
      Sort sort = Sort.by(Sort.Direction.ASC, "mes");
      return unicostServicio.findAll(sort);
   }

}

