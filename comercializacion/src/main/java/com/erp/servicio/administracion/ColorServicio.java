package com.erp.servicio.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.administracion.Colores;
import com.erp.repositorio.administracion.ColoresR;

@Service
public class ColorServicio {

   @Autowired
   ColoresR dao;

   public List<Colores> findTonos() {
      return dao.findTonos();
   }

   public List<Colores> findByTono(String codigo) {
      return dao.findByTono(codigo);
   }

}
