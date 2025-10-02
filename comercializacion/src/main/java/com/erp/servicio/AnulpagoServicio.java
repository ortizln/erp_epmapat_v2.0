package com.erp.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.controlador.Anulpago;
import com.erp.repositorio.AnulpagoR;

@Service
public class AnulpagoServicio {

   @Autowired
   private AnulpagoR dao;

   public Optional<Anulpago> findById(Long id) {
      return dao.findById(id);
   }

}