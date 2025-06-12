package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Cajas;
import com.erp.comercializacion.repositories.CajasR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CajasService {
    @Autowired
    private CajasR dao;

    public List<Cajas> findAll() {
        return dao.find_All();
    }

    public Optional<Cajas> findById(Long id) {
        return dao.findById(id);
    }

    public List<Cajas> findByCodigos(Long idptoemision, String codigo) {
        return dao.findByCodigos(idptoemision, codigo);
    }

    public List<Cajas> findByDescri(String descripcion) {
        return dao.findByDescri(descripcion);
    }

    public List<Cajas> findByIdptoemision(Long idptoemision) {
        return dao.findByIdptoemision(idptoemision);
    }

    public Cajas findCajaByIdUsuario(Long idusuario) {

        return dao.findCajaByIdUsuario(idusuario);
    }

    public List<Cajas> findCajasActivas() {
        return dao.findCajasActivas();
    }
    public <S extends Cajas> S save(S entity) {
        return dao.save(entity);
    }
    public void deleteById(Long id) {
        dao.deleteById(id);
    }
    public void delete(Cajas entity) {
        dao.delete(entity);
    }

}
