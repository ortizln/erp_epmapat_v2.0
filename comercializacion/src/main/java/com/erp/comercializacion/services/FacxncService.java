package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.NtaCreditoCompPago;
import com.erp.comercializacion.models.Facxnc;
import com.erp.comercializacion.repositories.FacxncR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacxncService {
    @Autowired
    private FacxncR dao;

    public Facxnc save(Facxnc fxnc){
        return dao.save(fxnc);
    }
    public List<Facxnc> findByNc(Long idvalcn){
        return null;
    }
    public List<NtaCreditoCompPago> findByIdfactura(Long idfactura) {
        return dao.findByIdfactura(idfactura);
    }
}
