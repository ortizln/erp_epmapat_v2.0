package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Rubros;
import com.erp.comercializacion.models.Tramites1;
import com.erp.comercializacion.repositories.Tramites1R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Tramites1Service {
    @Autowired
    private Tramites1R tramites1R;
    public List<Tramites1> findAll() {

        return tramites1R.findAll();
    }
    public List<Tramites1> findAll(Sort sort) {

        return tramites1R.findAll(sort);
    }
    public <S extends Tramites1> S save(S entity) {

        return tramites1R.save(entity);
    }
    public Optional<Tramites1> findById(Long id) {
        return tramites1R.findById(id);
    }
    public void deleteById(Long id) {
        tramites1R.deleteById(id);
    }


}
