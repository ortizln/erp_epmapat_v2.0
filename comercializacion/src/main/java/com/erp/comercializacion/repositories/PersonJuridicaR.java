package com.erp.comercializacion
.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

import com.erp.comercializacion.models.PersonJuridica;

// @Repository
public interface PersonJuridicaR extends JpaRepository<PersonJuridica, Long>{
	
}
