package com.erp.rrhh.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.ThAuditLog;

public interface ThAuditLogR extends JpaRepository<ThAuditLog, Long> {}
