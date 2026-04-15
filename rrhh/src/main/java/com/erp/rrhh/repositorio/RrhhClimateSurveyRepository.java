package com.erp.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erp.rrhh.modelo.RrhhClimateSurvey;

public interface RrhhClimateSurveyRepository extends JpaRepository<RrhhClimateSurvey, UUID>, JpaSpecificationExecutor<RrhhClimateSurvey> {
}

