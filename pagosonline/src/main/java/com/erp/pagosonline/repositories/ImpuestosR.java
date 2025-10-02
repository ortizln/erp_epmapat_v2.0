package com.erp.pagosonline.repositories;

import com.erp.pagosonline.models.Impuestos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ImpuestosR extends JpaRepository<Impuestos, Long> {
    @Query(value = "select * from impuestos where estado = ?1", nativeQuery = true)
    Impuestos getImpuestoByEstado(Boolean estado);
}
