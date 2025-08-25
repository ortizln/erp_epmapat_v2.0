package com.erp.reportes_jr.repositories;

import com.erp.reportes_jr.modals.Reportes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportesR extends JpaRepository<Reportes, Long> {
    Optional<Reportes> findByNombre(String nombre);

}
