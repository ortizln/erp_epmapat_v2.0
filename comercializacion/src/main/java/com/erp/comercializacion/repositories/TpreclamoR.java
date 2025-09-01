package com.erp.comercializacion
.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;

import com.erp.comercializacion.models.Tpreclamo;

//@Repository
public interface TpreclamoR extends JpaRepository<Tpreclamo, Long>{
    
}
