package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Categorias;
import com.erp.comercializacion.repositories.CategoriasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class CategoriasService {
    @Autowired
    private CategoriasR dao;

    //Categorias habilitadas
    public List<String> listaCategorias() {
        return dao.listaCategorias();
    }


    public List<Categorias> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public <S extends Categorias> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Categorias> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteByIdQ(id);
    }

    public List<Categorias> used(Long id) {
        return dao.used(id);
    }

    public List<Categorias> findByDescri(String descripcion) {
        return dao.findByDescri(descripcion);
    }

    //Suma totaltarifa
    public BigDecimal sumTotalTarifa() {
        return dao.sumTotalTarifa();
    }
}
