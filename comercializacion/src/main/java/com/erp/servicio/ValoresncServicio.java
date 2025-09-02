package com.erp.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.Valoresnc;
import com.erp.repositorio.ValoresncR;

@Service
public class ValoresncServicio {
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
