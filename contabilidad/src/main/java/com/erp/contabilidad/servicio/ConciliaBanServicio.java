package com.erp.contabilidad.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.contabilidad.modelo.ConciliaBan;
import com.erp.contabilidad.modelo.Cuentas;
import com.erp.contabilidad.repositorio.ConciliaBanR;

@Service
public class ConciliaBanServicio {

	@Autowired
	public ConciliaBanR dao;

	public List<ConciliaBan> findAll() {
		return dao.findAll();
	}

	public ConciliaBan getConciliaBan(Cuentas idcuenta, Integer mes) {
		return dao.findByIdcuentaAndMes(idcuenta, mes);
	 }

	public <S extends ConciliaBan> S save(S entity) {
		return dao.save(entity);
	}

	public void deleteById(Long id) {
	}
	
}

