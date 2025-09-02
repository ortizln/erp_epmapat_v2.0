package com.erp.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.erp.modelo.contabilidad.Tabla10;
import com.erp.repositorio.contabilidad.Tabla10R;

@Service
public class Tabla10Servicio {

    @Autowired
    private Tabla10R dao;

    public List<Tabla10> findAll() {
        return dao.findAll();
    }

    public Optional<Tabla10> findById(Long id) {
        return dao.findById(id);
    }

    public List<Tabla10> findByCodretair(String codretair) {
        return dao.findByCodretair(codretair);
    }

    public <S extends Tabla10> S save(S entity) {
        return dao.save(entity);
    }

    public void deleteById(Long id) {
		dao.deleteById(id);
	}

}
