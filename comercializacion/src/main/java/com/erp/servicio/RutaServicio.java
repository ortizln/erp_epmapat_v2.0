package com.erp.servicio;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.erp.interfaces.RutasInterfaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Service;

import com.erp.interfaces.CuentasByRutas;
import com.erp.modelo.Rutas;
import com.erp.repositorio.RutasR;

@Service
public class RutaServicio {
	@Autowired
	private RutasR dao;

	public List<Rutas> findAll() {
		return dao.findAll();
	}

	public List<Rutas> findAll(Sort sort) {
		return dao.findAll(sort);
	}

	public List<Rutas> findAllActive() {
		return dao.findAllActive();
	}

	public <S extends Rutas> S save(S entity) {

		return dao.save(entity);
	}

	public Optional<Rutas> findById(Long id) {

		return dao.findById(id);
	}

	public void deleteById(Long id) {

		dao.deleteById(id);
	}

	public void delete(Rutas entity) {

		dao.delete(entity);
	}

	// Valida Identificacion
	public boolean valCodigo(String codigo) {
		return dao.valCodigo(codigo);
	}

	public List<CuentasByRutas> getNcuentasByRutas() {
		return dao.getNcuentasByRutas();
	}

    public List<RutasInterfaces> getDeudaOfCuentasByIdrutas(Long idruta){
        return dao.getDeudaOfCuentasByIdrutas(idruta);
    }
    public List<RutasInterfaces> getDeudasOfAllCuentas(){
        return dao.getDeudasOfAllCuentas();
    }
    public List<RutasInterfaces> getDeudasOfRutas(){
        return dao.getDeudasOfRutas();
    }

}
