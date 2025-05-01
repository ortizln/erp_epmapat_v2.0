package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Valoresnc;
import com.erp.comercializacion.repositories.ValoresncR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ValoresncService {
    @Autowired
    private ValoresncR dao;

    public List<Valoresnc> findAll() {
        return dao.findAll();
    }

    public Valoresnc save(Valoresnc ntacredito) {
        return dao.save(ntacredito);
    }

    public Optional<Valoresnc> findById(Long idntacredito) {
        return dao.findById(idntacredito);
    }

}
