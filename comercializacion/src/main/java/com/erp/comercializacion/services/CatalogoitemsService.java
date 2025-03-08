package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Catalogoitems;
import com.erp.comercializacion.repositories.CatalogoitemsR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogoitemsService {
    @Autowired
    private CatalogoitemsR dao;

    public List<Catalogoitems> findProductos(Long idmodulo1, Long idmodulo2, String descripcion) {
        return dao.findProductos(idmodulo1, idmodulo2, descripcion);
    }
    public List<Catalogoitems> findAll() {
        return dao.findAll();
    }
    public List<Catalogoitems> findByIdrubro(Long idrubro) {
        return dao.findByIdrubro(idrubro);
    }
    public List<Catalogoitems> findByIdusoitems(Long idusoitems) {
        return dao.findByIdusoitems(idusoitems);
    }
    public Optional<Catalogoitems> findById(Long id) {
        return dao.findById(id);
    }
    public List<Catalogoitems> findByNombre(Long idusoitems, String descripcion) {
        return dao.findByNombre(idusoitems, descripcion);
    }
    public <S extends Catalogoitems> S save(S entity) {
        return dao.save(entity);
    }

}
