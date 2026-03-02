package com.erp.rrhh.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.ThLeaveBalance;

public interface ThLeaveBalanceR extends JpaRepository<ThLeaveBalance, Long> {
    List<ThLeaveBalance> findByIdpersonal_personal_IdpersonalOrderByAnioDesc(Long idpersonal);
    Optional<ThLeaveBalance> findByIdpersonal_personal_IdpersonalAndAnio(Long idpersonal, Integer anio);
}
