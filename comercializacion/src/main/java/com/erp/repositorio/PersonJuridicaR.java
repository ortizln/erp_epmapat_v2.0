package com.erp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

import com.erp.modelo.PersonJuridica;

// @Repository
public interface PersonJuridicaR extends JpaRepository<PersonJuridica, Long>{
	
}
