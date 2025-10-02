package com.erp.sri.service;

import com.erp.sri.model.Recaudacion;
import com.erp.sri.repository.Recaudacion_rep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Recaudacion_ser {
    @Autowired
    private Recaudacion_rep r_dao;
    @Autowired
    private Formacobro_ser s_formacobro;

    public Recaudacion save(Recaudacion r){
        Boolean fc = s_formacobro.getValidationFc(r.getFormapago());
        System.out.println("true "+fc);
        //VALIDACION DE LA FORMA DE COBRO
        //SI SI EXISTE PUEDO CONTINUAR Y GUARDAR LA RECAUDACION CASO CONTRARIO RETORNAR NULLO
        try{
            System.out.println(r.getEstado());
            return r_dao.save(r);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
