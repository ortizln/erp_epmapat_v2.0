package com.erp.servicio.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.erp.modelo.contabilidad.Unicostos;
import com.erp.repositorio.contabilidad.UnicostosR;

@Service
public class UnicostosServicio {

   @Autowired
   UnicostosR dao;

   public List<Unicostos> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   public <S extends Unicostos> S save(S entity) {
      return dao.save(entity);
   }

}
