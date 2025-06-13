package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Precioxcat;
import com.erp.comercializacion.repositories.PrecioxcatR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrecioxcatService {
    @Autowired
    private PrecioxcatR dao;

    public List<Precioxcat> findAll(Long c, Long dm, Long hm) {
        if(c != null && dm != null || hm != null) {
            return dao.findAll(c, dm,hm);
        }
        else {
            return dao.findAll();
        }
    }

    public List<Precioxcat> findConsumo(Long idcategoria, Long m3 ) {
        if(idcategoria != null && m3 != null ) {
            return dao.findConsumo(idcategoria, m3);
        }
        else {
            return null;
        }
    }

    public <S extends Precioxcat> S save(S entity) {
        return dao.save(entity);
    }

    public Optional<Precioxcat> findById(Long id) {
        return dao.findById(id);
    }

    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    public void delete(Precioxcat entity) {
        dao.delete(entity);
    }
}
