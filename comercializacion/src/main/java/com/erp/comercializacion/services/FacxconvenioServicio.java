package com.erp.comercializacion.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.Facxconvenio;
import com.erp.comercializacion.repositories.FacxconvenioR;

@Service

public class FacxconvenioServicio {

   @Autowired
   FacxconvenioR dao;

   // Solo para brobar en Postman
   public List<Facxconvenio> find10() {
      return dao.find10();
   }

   // Facturas por Convenio
   public List<Facxconvenio> findByConvenio(Long idconvenio) {
      return dao.findByConvenio(idconvenio);
   }

   // Solo para probar en Postman
   public Optional<Facxconvenio> findById(Long idfacxconvenio) {
      return dao.findById(idfacxconvenio);
   }

   public <S extends Facxconvenio> S save(S entity) {
      return dao.save(entity);
   }

}
