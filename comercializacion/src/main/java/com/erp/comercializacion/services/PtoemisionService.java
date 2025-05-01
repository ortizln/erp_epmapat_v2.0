package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Ptoemision;
import com.erp.comercializacion.repositories.PtoemisionR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PtoemisionService {
    @Autowired
    private PtoemisionR ptoemisionR;

    public List<Ptoemision> findAll(Sort sort) {
        return ptoemisionR.findAll(sort);
    }

    public <S extends Ptoemision> S save(S entity) {
        return ptoemisionR.save(entity);
    }

    public void deleteById(Long id) {
        ptoemisionR.deleteByIdQ(id);
    }

    public void delete(Ptoemision entity) {
        ptoemisionR.delete(entity);
    }

    public Optional<Ptoemision> findById(Long id) {
        return ptoemisionR.findById(id);
    }

    public List<Ptoemision> used(Long id) {
        return ptoemisionR.used(id);
    }
}
