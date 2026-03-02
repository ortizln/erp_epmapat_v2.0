package com.erp.contabilidad.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.Pagoscobros;
import com.erp.contabilidad.repositorio.PagoscobrosR;

@Service
public class PagoscobroServicio {

   @Autowired
   private PagoscobrosR dao;

   public List<Pagoscobros> findByIdbenxtra(Long idbenxtra) {
      return dao.getByIdbenxtra(idbenxtra);
   }

}

