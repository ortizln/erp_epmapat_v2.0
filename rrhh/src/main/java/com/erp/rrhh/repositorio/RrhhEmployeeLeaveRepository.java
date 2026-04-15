package com.erp.rrhh.repositorio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.RrhhEmployeeLeave;

public interface RrhhEmployeeLeaveRepository extends JpaRepository<RrhhEmployeeLeave, UUID> {
    List<RrhhEmployeeLeave> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
}

