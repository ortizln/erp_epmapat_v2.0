package com.erp.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.contabilidad.Tabla01;
import com.erp.repositorio.contabilidad.Tabla01R;

@Service
public class Tabla01Servicio {

    @Autowired
    private Tabla01R dao;

    public List<Tabla01> findAll() {
        return dao.findAll();
    }

    public Optional<Tabla01> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Tabla01> S save(S entity) {
        return dao.save(entity);
    }

    public void deleteById(Long id) {
		dao.deleteById(id);
	}

}
