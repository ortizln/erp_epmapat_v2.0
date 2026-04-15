package com.erp.rrhh.repositorio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.RrhhEmployeeContract;

public interface RrhhEmployeeContractRepository extends JpaRepository<RrhhEmployeeContract, UUID> {
    List<RrhhEmployeeContract> findByEmployeeIdOrderByStartDateDesc(Long employeeId);
    boolean existsByEmployeeIdAndCurrentContractTrue(Long employeeId);
    boolean existsByEmployeeIdAndCurrentContractTrueAndIdNot(Long employeeId, UUID id);
}

