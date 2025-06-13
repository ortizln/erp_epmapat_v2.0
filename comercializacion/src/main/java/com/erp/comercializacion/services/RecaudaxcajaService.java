package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Recaudaxcaja;
import com.erp.comercializacion.repositories.RecaudaxcajaR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RecaudaxcajaService {
    @Autowired
    private RecaudaxcajaR dao;
    // Busca por Caja y fechas
    public List<Recaudaxcaja> findByCaja(Long idcaja, Date desde, Date hasta) {
        return dao.findByCaja(idcaja, desde, hasta);
    }

    public Recaudaxcaja findLastConexion(Long idcaja) {
        return dao.findLastConexion(idcaja);
    }
    public <S extends Recaudaxcaja> S save(S entity) {
        return dao.save(entity);
    }
    public Optional<Recaudaxcaja> findByIdrecaudaxcaja(Long idrecaudaxcaja){
        return dao.findById(idrecaudaxcaja);
    }
}
