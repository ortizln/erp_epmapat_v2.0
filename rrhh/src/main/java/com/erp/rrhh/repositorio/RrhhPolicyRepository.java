package com.erp.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erp.rrhh.modelo.RrhhPolicy;

public interface RrhhPolicyRepository extends JpaRepository<RrhhPolicy, UUID>, JpaSpecificationExecutor<RrhhPolicy> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
}

