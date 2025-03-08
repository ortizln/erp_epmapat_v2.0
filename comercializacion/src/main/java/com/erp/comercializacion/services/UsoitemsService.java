package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Usoitems;
import com.erp.comercializacion.repositories.UsoitemsR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsoitemsService {
    @Autowired
    private UsoitemsR dao;

    public List<Usoitems> findByOrderByDescripcionAsc() {
        return dao.findByOrderByDescripcionAsc();
    }

    public List<Usoitems> findByIdmodulo(Long idmodulo) {
        return dao.findByIdmodulo(idmodulo);
    }

    public Optional<Usoitems> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Usoitems> S save(S entity) {
        return dao.save(entity);
    }

    public List<Usoitems> findByNombre(Long idmodulo, String descripcion) {
        return dao.findByNombre(idmodulo, descripcion);
    }
}
