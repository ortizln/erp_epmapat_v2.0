package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.NtaCreditoSaldos;
import com.erp.comercializacion.models.Ntacredito;
import com.erp.comercializacion.repositories.NtacreditoR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NtacreditoService {
    @Autowired
    private NtacreditoR dao;

    public List<Ntacredito> findAll() {
        return dao.findAll();
    }

    public Ntacredito save(Ntacredito ntacredito) {
        return dao.save(ntacredito);
    }

    public Optional<Ntacredito> findById(Long idntacredito) {
        return dao.findById(idntacredito);
    }
    public Page<Ntacredito> findAllNtaCredito(int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return dao.findAllNtaCreditos(pageable);
        //return null;
    }
    public List<NtaCreditoSaldos> findSaldosByCuenta(Long cuenta){
        return dao.findSaldosByCuenta(cuenta);
    }
}
