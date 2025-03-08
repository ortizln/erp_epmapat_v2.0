package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Novedades;
import com.erp.comercializacion.repositories.NovedadesR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NovedadesService {
    @Autowired
    private NovedadesR dao;
    public List<Novedades> findByDescri(String descripcion) {
        return dao.findByDescri(descripcion) ;
    }
    public List<Novedades> findAll() {
        return dao.findAll();
    }
    public <S extends Novedades> S save(S entity) {
        return dao.save(entity);
    }
    public Optional<Novedades> findById(Long id) {
        return dao.findById(id);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
    public List<Novedades> findByEstado(Long estado) {
        return dao.findByEstado(estado);
    }
}
