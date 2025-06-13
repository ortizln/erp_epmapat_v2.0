package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.models.Tpcertifica;
import com.erp.comercializacion.services.TpcertificaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tpcertifica")
@CrossOrigin("*")

public class TpCertificaApi {

   @Autowired
   private TpcertificaService tpServicio;

   @GetMapping
   public List<Tpcertifica> getAll() {
      return tpServicio.findAll();
   }

}
