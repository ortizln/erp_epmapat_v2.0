package com.erp.comercializacion.services;

import com.erp.comercializacion.models.Servicios1;
import com.erp.comercializacion.repositories.Servicios1R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Servicios1Service {

    @Autowired
    private Servicios1R servicios1R;

    public List<Servicios1> findAll() {
        return servicios1R.findAll();
    }

    public List<Servicios1> findAll(Sort sort) {
        return servicios1R.findAll(sort);
    }
    public <S extends Servicios1> S save(S entity) {

        return servicios1R.save(entity);
    }
    public Optional<Servicios1> findById(Long id) {

        return servicios1R.findById(id);
    }
    public void deleteById(Long id) {

        servicios1R.deleteById(id);
    }

    public void delete(Servicios1 entity) {

        servicios1R.delete(entity);
    }
    public List<Servicios1> findByIdModulos(Long idmodulo) {

        return servicios1R.findByIdModulos(idmodulo);
    }
}
