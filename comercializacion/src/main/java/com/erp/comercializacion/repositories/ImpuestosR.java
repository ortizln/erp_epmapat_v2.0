package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Impuestos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImpuestosR extends JpaRepository<Impuestos, Long> {
    @Query(value = "SELECT * FROM impuestos where estado = true", nativeQuery = true)
    Impuestos getCurrentImpuesto();

}
