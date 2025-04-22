package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Ctramites;
import com.erp.comercializacion.repositories.CtramitesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CtramitesService {
    @Autowired
    private CtramitesR dao;
    public List<Ctramites> findAll() {
        return dao.findAll();
    }

    public List<Ctramites> findByTpTramite(Long idTpTramite) {
        return dao.findByTpTramite(idTpTramite);
    }

    public List<Ctramites> findByDescripcion(String descripcion) {
        return dao.findByDescripcion(descripcion);
    }
    public List<Ctramites> findByfeccrea(Date feccrea) {
        return dao.findByfeccrea(feccrea);
    }

    public List<Ctramites> findAll(Sort sort) {
        return dao.findAll(sort);
    }
    //Trámites por Cliente
    public List<Ctramites> findByIdcliente(Long idcliente) {
        return dao.findByIdcliente(idcliente);
    }

    public <S extends Ctramites> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Ctramites> findById(Long id) {
        return dao.findById(id);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }


}
