package com.erp.recaudacion.service;

import com.erp.recaudacion.repository.Formacobro_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Formacobro_ser {
    @Autowired
    private Formacobro_rep fc_dao;
    public Boolean getValidationFc(Long idformacobro){
        return fc_dao.valFormaCobro(idformacobro) == null;
    }
}
