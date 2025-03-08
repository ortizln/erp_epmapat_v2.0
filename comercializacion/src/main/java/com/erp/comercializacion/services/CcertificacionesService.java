package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Ccertificaciones;
import com.erp.comercializacion.repositories.CcertificacionesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CcertificacionesService {
    @Autowired
    private CcertificacionesR dao;

    public List<Ccertificaciones> findDesdeHasta(Long desde, Long hasta) {
        if (desde != null || hasta != null) {
            return dao.findDesdeHasta(desde, hasta);
        } else {
            return null;
        }
    }

    // Busca por Cliente
    public List<Ccertificaciones> findByCliente(String cliente) {
        return dao.findByCliente(cliente);
    }

    public Ccertificaciones ultima() {
        return dao.findFirstByOrderByIdccertificacionDesc();
    }

    public <S extends Ccertificaciones> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Ccertificaciones> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public void delete(Ccertificaciones entity) {
        dao.delete(entity);
    }

    public <S extends Ccertificaciones> boolean exists(Example<S> example) {
        return false;
    }

}
