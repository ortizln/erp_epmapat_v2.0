package com.erp.contabilidad.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.ConciliaBan;
import com.erp.contabilidad.repositorio.ConciliaBanR;

@Service
public class ConciliaBanS {
	@Autowired
	public ConciliaBanR conciliabanR;

	public List<ConciliaBan> findAll() {
		
		return conciliabanR.findAll();
	}

	public <S extends ConciliaBan> S save(S entity) {
		
		return conciliabanR.save(entity);
	}

	public Optional<ConciliaBan> findById(Long id) {
		
		return Optional.empty();
	}
	public boolean existsById(Long id) {
		
		return false;
	}
	
}

