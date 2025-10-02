package com.erp.repositorio.rrhh;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.rrhh.Cargos;

public interface CargosR extends JpaRepository<Cargos, Long> {
    
}
