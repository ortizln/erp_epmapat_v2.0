package com.erp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.Impuestos;

public interface ImpuestosR extends JpaRepository<Impuestos, Long> {
    @Query(value = "SELECT * FROM impuestos where estado = true", nativeQuery = true)
    Impuestos getCurrentImpuesto();
}
