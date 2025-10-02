package com.erp.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.Formacobro;
import com.erp.repositorio.FormacobroR;

@Service
public class FormacobroServicio {

   @Autowired
	private FormacobroR dao;

    public List<Formacobro> findAll() {
        return dao.findAll();
    }
    
}
