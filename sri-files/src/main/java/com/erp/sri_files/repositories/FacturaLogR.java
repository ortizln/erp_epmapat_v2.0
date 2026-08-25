package com.erp.sri_files.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.sri_files.models.FacturaLog;

public interface FacturaLogR extends JpaRepository<FacturaLog, Long> {
    
    List<FacturaLog> findByIdfacturaOrderByFechaDesc(Long idfactura);
    
    @Query("SELECT fl FROM FacturaLog fl WHERE fl.idfactura = :idfactura AND fl.estado = :estado ORDER BY fl.fecha DESC")
    List<FacturaLog> findByIdfacturaAndEstado(@Param("idfactura") Long idfactura, @Param("estado") String estado);
}
