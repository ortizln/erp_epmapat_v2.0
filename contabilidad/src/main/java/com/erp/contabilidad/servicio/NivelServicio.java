package com.erp.contabilidad.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.Niveles;
import com.erp.contabilidad.repositorio.NivelesR;

@Service
public class NivelServicio {

	@Autowired
	private NivelesR dao;

	public List<Niveles> findAll() {
		return dao.findAll();
	}

	
	public Optional<Niveles> findById(Long id) {
		return dao.findById(id);
	}

	public Niveles findByNivcue(Integer nivcue) {
		return dao.findByNivcue(nivcue);
	}

	// Obtiene el siguiente registro de un nivcue dado
	public Niveles findSiguienteByNivcue(Integer nivcue) {
		return dao.findSiguienteByNivcue(nivcue);
	}

}

