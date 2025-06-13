package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Reclamos;
import com.erp.comercializacion.repositories.ReclamosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReclamosService {
    @Autowired
    private ReclamosR reclamosR;
    public List<Reclamos> findAll() {
        return reclamosR.findAll();
    }
    public <S extends Reclamos> S save(S entity) {
        return reclamosR.save(entity);
    }
    public Optional<Reclamos> findById(Long id) {
        return reclamosR.findById(id);
    }
    public void deleteById(Long id) {
        reclamosR.deleteById(id);
    }
    public void delete(Reclamos entity) {
        reclamosR.delete(entity);
    }

}
