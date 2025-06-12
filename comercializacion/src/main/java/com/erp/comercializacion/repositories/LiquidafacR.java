package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Liquidafac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LiquidafacR extends JpaRepository<Liquidafac, Long> {
    @Query(value = "SELECT * FROM liquidafac WHERE idfacturacion_facturacion =?1 order by idliquidafac", nativeQuery = true)
    public List<Liquidafac> findByIdfacturacion(Long idfacturacion);
}
