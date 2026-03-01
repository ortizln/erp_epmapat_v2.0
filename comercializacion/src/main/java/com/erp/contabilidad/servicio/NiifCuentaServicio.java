package com.erp.contabilidad.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.NiifCuentas;
import com.erp.contabilidad.repositorio.NiifCuentasR;

@Service
public class NiifCuentaServicio {

   @Autowired
   private NiifCuentasR dao;

   public List<NiifCuentas> findAll() {
      return dao.findAll();
   }

   public List<NiifCuentas> findByCodigoyNombre(String codcue, String nomcue) {
      return dao.findByCodigoyNombre(codcue, nomcue);
   }

   public <S extends NiifCuentas> S save(S entity) {
		return dao.save(entity);
	}

   public Optional<NiifCuentas> findById(Long id) {
		return dao.findById(id);
	}

}

