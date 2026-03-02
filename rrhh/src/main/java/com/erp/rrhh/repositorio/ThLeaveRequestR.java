package com.erp.rrhh.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.ThLeaveRequest;

public interface ThLeaveRequestR extends JpaRepository<ThLeaveRequest, Long> {
    List<ThLeaveRequest> findByIdpersonal_personal_IdpersonalOrderByFeccreaDesc(Long idpersonal);
    List<ThLeaveRequest> findByEstadoOrderByFeccreaDesc(String estado);
}
