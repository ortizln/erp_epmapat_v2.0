package com.erp.comercializacion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.comercializacion.models.TpCertifica;
import com.erp.comercializacion.services.TpCertificaServicio;

@RestController
@RequestMapping("/tpcertifica")
@CrossOrigin("*")

public class TpCertificaApi {

   @Autowired
   private TpCertificaServicio tpServicio;

   @GetMapping
   public List<TpCertifica> getAll() {
      return tpServicio.findAll();
   }

}
