package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Clasificador;
import com.erp.comercializacion.repositories.ClasificadorR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClasificadorService {
    @Autowired
   private  ClasificadorR dao;

    public List<Clasificador> buscaByCodigo(String codpar) {
        return dao.buscaByCodigo(codpar);
    }

    public List<Clasificador> buscaByNombre(String nombre) {
        return dao.buscaByNombre(nombre);
    }

    //Partidas de Ingresos
    public List<Clasificador> buscaParingreso(String codpar, String nompar){
        return dao.buscaParingreso(codpar, nompar);
    }

    //Partidas de Gastos, por Código o Nombre (en un mismo campo)
    public List<Clasificador> findPartidasG(String codigoNombre ){
        return dao.findPartidasG( codigoNombre );
    }

    public <S extends Clasificador> S save(S entity) {
        return dao.save(entity);
    }

    public List<Clasificador> findAll(Sort sort) {
        return dao.findAll(sort);
    }

    public Page<Clasificador> findAll(Pageable pageable) {
        return dao.findAll(pageable);
    }

    public List<Clasificador> findAll() {
        return dao.findAll();
    }

    public Optional<Clasificador> findById(Long id) {
        return dao.findById(id);
    }

    public Boolean deleteById(Long id) {
        if (dao.existsById(id)) {
            dao.deleteById(id);
            return !dao.existsById(id);
        }
        return false;
    }

    public void delete(Clasificador entity) {
        dao.delete(entity);
    }
}
