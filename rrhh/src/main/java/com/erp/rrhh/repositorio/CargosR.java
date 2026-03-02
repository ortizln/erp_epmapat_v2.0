package com.erp.rrhh.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.Cargos;

public interface CargosR extends JpaRepository<Cargos, Long> {
    
}

