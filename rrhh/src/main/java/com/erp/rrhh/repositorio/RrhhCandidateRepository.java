package com.erp.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erp.rrhh.modelo.RrhhCandidate;

public interface RrhhCandidateRepository extends JpaRepository<RrhhCandidate, UUID>, JpaSpecificationExecutor<RrhhCandidate> {
    boolean existsByIdentification(String identification);
    boolean existsByIdentificationAndIdNot(String identification, UUID id);
}

