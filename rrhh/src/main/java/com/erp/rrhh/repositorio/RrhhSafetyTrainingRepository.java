package com.erp.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erp.rrhh.modelo.RrhhSafetyTraining;

public interface RrhhSafetyTrainingRepository extends JpaRepository<RrhhSafetyTraining, UUID>, JpaSpecificationExecutor<RrhhSafetyTraining> {
}

