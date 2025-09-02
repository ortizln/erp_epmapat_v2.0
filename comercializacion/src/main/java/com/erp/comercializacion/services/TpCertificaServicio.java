package com.erp.comercializacion.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.TpCertifica;
import com.erp.comercializacion.repositories.TpCertificaR;

@Service
public class TpCertificaServicio {

	@Autowired
	TpCertificaR dao;

	public List<TpCertifica> findAll() {
		return dao.findAll();
	}
}
