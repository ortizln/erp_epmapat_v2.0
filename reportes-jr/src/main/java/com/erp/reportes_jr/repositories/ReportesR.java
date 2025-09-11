package com.erp.reportes_jr.repositories;

import com.erp.reportes_jr.modelo.Reportes;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportesR extends JpaRepository<Reportes, Long> {
    Reportes findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}
