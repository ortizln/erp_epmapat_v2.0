package com.erp.rrhh.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.ThAuditLog;

public interface ThAuditLogR extends JpaRepository<ThAuditLog, Long> {
    List<ThAuditLog> findByEntidadOrderByFechaDesc(String entidad);
    List<ThAuditLog> findByEntidadAndIdregistroOrderByFechaDesc(String entidad, Long idregistro);
}
