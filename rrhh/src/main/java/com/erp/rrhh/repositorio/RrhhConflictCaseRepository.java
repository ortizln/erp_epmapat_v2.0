package com.erp.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erp.rrhh.modelo.RrhhConflictCase;

public interface RrhhConflictCaseRepository extends JpaRepository<RrhhConflictCase, UUID>, JpaSpecificationExecutor<RrhhConflictCase> {
}

