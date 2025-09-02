package com.erp.servicio.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.contabilidad.Pagoscobros;
import com.erp.repositorio.contabilidad.PagoscobrosR;

@Service
public class PagoscobroServicio {

   @Autowired
   private PagoscobrosR dao;

   public List<Pagoscobros> findByIdbenxtra(Long idbenxtra) {
      return dao.getByIdbenxtra(idbenxtra);
   }

}
