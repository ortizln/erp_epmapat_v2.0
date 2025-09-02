package com.erp.comercializacion.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.Aguatramite;
import com.erp.comercializacion.repositories.AguaTramiteR;

@Service

public class AguaTramiteServicio {

   @Autowired
   private AguaTramiteR dao;

   public List<Aguatramite> findAll(Long desde, Long hasta) {
      if (desde != null || hasta != null) {
         return dao.findAll(desde, hasta);
      } else {
         return dao.findAll();
      }
   }

   public List<Aguatramite> findByIdTipTramite(Long idtipotramite, Long estado, Date d, Date h) {
      return dao.findByIdTipTramite(idtipotramite, estado, d, h, d, h);
   }

   public List<Aguatramite> findByNombre(String nombre) {
      return dao.findByCliente(nombre);
   }

   public Optional<Aguatramite> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends Aguatramite> S save(S entity) {
      return dao.save(entity);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}
