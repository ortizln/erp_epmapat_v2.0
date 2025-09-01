package com.erp.comercializacion
.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.controlador.Anulpago;
import com.erp.comercializacion.repositories.AnulpagoR;

@Service
public class AnulpagoServicio {

   @Autowired
   private AnulpagoR dao;

   public Optional<Anulpago> findById(Long id) {
      return dao.findById(id);
   }

}