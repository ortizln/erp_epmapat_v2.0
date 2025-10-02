package com.erp.servicio.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.contabilidad.Fec_retenciones;
import com.erp.repositorio.contabilidad.Fec_retencionesR;

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
