package com.erp.sri_files.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.sri_files.models.Factura;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FacturaR extends JpaRepository<Factura, Long>{
    Factura findByIdfactura(Long idfactura);
    @Query("SELECT f FROM Factura f WHERE UPPER(TRIM(COALESCE(f.estado, ''))) = UPPER(TRIM(:estado)) ORDER BY f.idfactura ASC")
    List<Factura> _findByEstado(@Param("estado") String estado, Pageable pageable);
    @Query("SELECT f FROM Factura f WHERE UPPER(TRIM(COALESCE(f.estado, ''))) = UPPER(TRIM(:estado)) ORDER BY f.idfactura ASC")
    Page<Factura> findByEstadoNormalizado(@Param("estado") String estado, Pageable pageable);

}
