package com.erp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

import com.erp.modelo.Estadom;

//@Repository
public interface EstadomR extends JpaRepository<Estadom, Long> {
    
}
