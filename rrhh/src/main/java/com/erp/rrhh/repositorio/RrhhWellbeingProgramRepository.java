package com.erp.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.erp.rrhh.modelo.RrhhWellbeingProgram;

public interface RrhhWellbeingProgramRepository extends JpaRepository<RrhhWellbeingProgram, UUID>, JpaSpecificationExecutor<RrhhWellbeingProgram> {
}

