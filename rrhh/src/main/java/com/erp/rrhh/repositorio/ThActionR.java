package com.erp.rrhh.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.ThAction;

public interface ThActionR extends JpaRepository<ThAction, Long> {
    List<ThAction> findByIdpersonal_personal_IdpersonalOrderByFeccreaDesc(Long idpersonal);
}
