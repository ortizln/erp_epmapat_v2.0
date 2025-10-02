package com.erp.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.TpCertifica;
import com.erp.repositorio.TpCertificaR;

@Service
public class TpCertificaServicio {
   
   @Autowired
	TpCertificaR dao;

   public List<TpCertifica> findAll() {
		return dao.findAll();
	}
}
