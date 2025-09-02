package com.erp.comercializacion.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.AguaTramite;
import com.erp.comercializacion.repositories.AguaTramiteR;

@Service

public class AguaTramiteServicio {

   @Autowired
   private AguaTramiteR dao;

   public List<AguaTramite> findAll(Long desde, Long hasta) {
      if (desde != null || hasta != null) {
         return dao.findAll(desde, hasta);
      } else {
         return dao.findAll();
      }
   }

   public List<AguaTramite> findByIdTipTramite(Long idtipotramite, Long estado, Date d, Date h) {
      return dao.findByIdTipTramite(idtipotramite, estado, d, h, d, h);
   }

   public List<AguaTramite> findByNombre(String nombre) {
      return dao.findByCliente(nombre);
   }

   public Optional<AguaTramite> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends AguaTramite> S save(S entity) {
      return dao.save(entity);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}
