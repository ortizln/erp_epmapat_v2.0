package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Liquidatramite;
import com.erp.comercializacion.repositories.LiquidaTramiteR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LiquidaTramiteService {
    @Autowired
    private LiquidaTramiteR liquidatramiteR;
    public List<Liquidatramite> findAll() {
        return liquidatramiteR.findAll();
    }
    public List<Liquidatramite> findAll(Sort sort) {
        return liquidatramiteR.findAll(sort);
    }

    public <S extends Liquidatramite> S save(S entity) {
        return liquidatramiteR.save(entity);
    }
    public Optional<Liquidatramite> findById(Long id) {

        return liquidatramiteR.findById(id);
    }
    public void deleteById(Long id) {
        liquidatramiteR.deleteById(id);
    }
    public List<Liquidatramite> findByIdTramite(Long idtramite) {
        return liquidatramiteR.findByIdTramite(idtramite);
    }
}
