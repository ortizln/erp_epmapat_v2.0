package com.erp.sri_files.batch;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchJobR extends JpaRepository<BatchJob, Long> {

    Optional<BatchJob> findByUuid(String uuid);

    List<BatchJob> findByEstadoOrderByFechaInicioDesc(String estado);

    @Query("SELECT b FROM BatchJob b WHERE b.estado IN ('PENDIENTE', 'EN_PROCESO') ORDER BY b.fechaInicio ASC")
    List<BatchJob> findActivos();

    @Query("SELECT b FROM BatchJob b ORDER BY b.fechaInicio DESC LIMIT :limit")
    List<BatchJob> findRecientes(@Param("limit") int limit);
}
