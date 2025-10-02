package com.erp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.Tipopago;

public interface TipopagoR extends JpaRepository<Tipopago, Long> {
    
}
