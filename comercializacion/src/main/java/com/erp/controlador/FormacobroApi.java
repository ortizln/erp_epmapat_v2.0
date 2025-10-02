package com.erp.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.modelo.Formacobro;
import com.erp.servicio.FormacobroServicio;

@RestController
@RequestMapping("/formacobro")
@CrossOrigin("*")

public class FormacobroApi {

   @Autowired
   FormacobroServicio fcobroServicio;

   @GetMapping
   public List<Formacobro> getAll() {
      return fcobroServicio.findAll();
   }

}
