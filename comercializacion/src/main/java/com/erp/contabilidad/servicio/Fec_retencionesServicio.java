package com.erp.contabilidad.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.Fec_retenciones;
import com.erp.contabilidad.repositorio.Fec_retencionesR;

@Service
public class Fec_retencionesServicio {
   
   @Autowired
   private Fec_retencionesR dao;

   public List<Fec_retenciones> findAll() {
      return dao.findAll();
   }

   public <S extends Fec_retenciones> S save(S entity) {
      return dao.save(entity);
   }

}

