package com.erp.comercializacion.servicio.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.modelo.administracion.Acceso;
import com.erp.comercializacion.repositorio.administracion.AccesoR;

@Service
public class AccesoServicio {

   @Autowired
   AccesoR dao;

   // Todos
   public List<Acceso> findAll() {
      return dao.findAll();
   }

}

