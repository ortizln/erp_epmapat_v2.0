package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.models.Formacobro;
import com.erp.comercializacion.services.FormacobroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/formacobro")
@CrossOrigin("*")

public class FormacobroApi {

   @Autowired
   private FormacobroService fcobroServicio;

   @GetMapping
   public List<Formacobro> getAll() {
      return fcobroServicio.findAll();
   }

}
