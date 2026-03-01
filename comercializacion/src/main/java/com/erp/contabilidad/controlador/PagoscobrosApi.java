package com.erp.contabilidad.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.contabilidad.modelo.Pagoscobros;
import com.erp.contabilidad.servicio.PagoscobroServicio;

@RestController
@RequestMapping("/api/pagoscobros")


public class PagoscobrosApi {

   @Autowired
   private PagoscobroServicio pagoscobroServicio;

   //Pagoscobros de una idbenxtra con ResponseEntity
   @GetMapping("/idbenxtra")
   public ResponseEntity<List<Pagoscobros>> getByIdBenxtra(@Param("idbenxtra") Long idbenxtra) {
      return ResponseEntity.ok(pagoscobroServicio.findByIdbenxtra(idbenxtra));
   }

   //Pagoscobros de una idbenxtra sin ResponseEntity
   @GetMapping
   public List<Pagoscobros> getByIdBenxtra1(@Param("idbenxtra") Long idbenxtra) {
      return pagoscobroServicio.findByIdbenxtra( idbenxtra );
   }

}

