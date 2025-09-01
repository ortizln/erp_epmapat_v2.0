package com.erp.comercializacion
.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

import com.erp.comercializacion.models.Reclamos;

// @Repository
public interface ReclamosR extends JpaRepository<Reclamos, Long>{

}
