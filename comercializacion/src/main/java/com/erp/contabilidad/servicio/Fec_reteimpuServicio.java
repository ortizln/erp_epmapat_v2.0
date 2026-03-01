package com.erp.contabilidad.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.Fec_reteimpu;
import com.erp.contabilidad.repositorio.Fec_reteimpuR;

@Service

public class Fec_reteimpuServicio {

   @Autowired
   private Fec_reteimpuR dao;

   public List<Fec_reteimpu> findAll() {
      return dao.findAll();
   }

   public <S extends Fec_reteimpu> S save(S entity) {
      return dao.save(entity);
   }

}

