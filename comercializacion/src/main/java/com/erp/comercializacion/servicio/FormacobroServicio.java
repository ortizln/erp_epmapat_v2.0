package com.erp.comercializacion.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.comercializacion.modelo.Formacobro;
import com.erp.comercializacion.repositorio.FormacobroR;

@Service
public class FormacobroServicio {

   @Autowired
	private FormacobroR dao;

    public List<Formacobro> findAll() {
        return dao.findAll();
    }
    
}


