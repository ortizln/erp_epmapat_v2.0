package com.erp.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.Modulos;
import com.erp.repositorio.ModulosR;

@Service
public class ModuloServicio {

	@Autowired
	private ModulosR dao;

	public List<Modulos> findAll() {
		return dao.findByOrderByDescripcionAsc();
	}

	public List<Modulos> getTwoModulos() {
		return dao.getTwoModulos();
	}

	// @Override
   public Optional<Modulos> findById(Long id) {
      return dao.findById(id);
   }

}
