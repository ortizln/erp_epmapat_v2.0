package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Rutasxemision;
import com.erp.comercializacion.repositories.RutasxemisionR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RutasxemisionService {
    @Autowired
    private RutasxemisionR dao;

    public List<Rutasxemision> findByIdemision(Long idemision) {
        return dao.findByIdemision(idemision);
    }

    public Optional<Rutasxemision> findById(Long idemision) {
        return dao.findById(idemision);
    }

    public <S extends Rutasxemision> S save(S entity) {
        return dao.save(entity);
    }

    public Long contarPorEstadoYEmision(Long idemision_emisiones) {
        return dao.contarPorEstadoYIdemision( idemision_emisiones );
    }
    public Rutasxemision findByEmisionRuta(Long idemision, Long idruta){
        return dao.findByEmisionRuta(idemision, idruta);
    }
}
