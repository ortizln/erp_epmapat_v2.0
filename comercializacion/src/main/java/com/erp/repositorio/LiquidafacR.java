package com.erp.repositorio;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.Liquidafac;

public interface LiquidafacR extends JpaRepository<Liquidafac, Serializable> {

   @Query(value = "SELECT * FROM liquidafac WHERE idfacturacion_facturacion =?1 order by idliquidafac", nativeQuery = true)
    public List<Liquidafac> findByIdfacturacion(Long idfacturacion);
        
}
