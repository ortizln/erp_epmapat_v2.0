package com.erp.comercializacion
.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.models.Cuotas;
import com.erp.comercializacion.repositories.CuotasR;

@Service
public class CuotaServicio {
   
   @Autowired
	private CuotasR dao;

   //Solo para brobar en Postman
	public List<Cuotas> find10() {
		return dao.find10();
	}

    public List<Cuotas> findByIdconvenio(Long idconvenio) {
        return dao.findByIdconvenio( idconvenio );
    }

    public <S extends Cuotas> S save(S entity) {
    	return dao.save(entity);
    }
}
