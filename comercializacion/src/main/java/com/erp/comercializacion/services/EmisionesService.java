package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.ResEmisiones;
import com.erp.comercializacion.models.Emisiones;
import com.erp.comercializacion.repositories.EmisionesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmisionesService {
    @Autowired
    private EmisionesR dao;

    public List<Emisiones> findByDesdeHasta(String desde, String hasta) {
        return dao.findByDesdeHasta(desde, hasta);
    }

    public <S extends Emisiones> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Emisiones> findById(Long id) {
        return dao.findById(id);
    }

    // Busca la última Emisión
    public Emisiones findFirstByOrderByEmisionDesc() {
        return dao.findFirstByOrderByEmisionDesc();
    }

    public List<Emisiones> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public List<Emisiones> findByIdEmisiones(Long idemision) {
        return dao.findByIdEmisiones(idemision);
    }
    public List<ResEmisiones> getResEmisiones(Long limit) {
        return dao.ResumenEmisiones(limit);
    }
}
