package com.erp.servicio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.contabilidad.Certipresu;
import com.erp.repositorio.contabilidad.CertipresuR;

@Service
public class CertipresuServicio {

	@Autowired
	private CertipresuR dao;

	public List<Certipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha) {
		return dao.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
	}

	public Certipresu findFirstByOrderByNumeroDesc() {
		return dao.findFirstByOrderByNumeroDesc();
	}

	public Certipresu findByNumeroAndTipo(Long numero, int tipo) {
		return dao.findByNumeroAndTipo(numero, tipo);
	}

	public Optional<Certipresu> findById(Long id) {
		return dao.findById(id);
	}

	public <S extends Certipresu> S save(S entity) {
		return dao.save(entity);
	}

	public void deleteById(Long id) {
		dao.deleteById(id);
	}

}
