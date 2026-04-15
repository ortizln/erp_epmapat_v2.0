package com.erp.rrhh.repositorio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.RrhhEmployeeFileRecord;

public interface RrhhEmployeeFileRecordRepository extends JpaRepository<RrhhEmployeeFileRecord, UUID> {
    List<RrhhEmployeeFileRecord> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}

