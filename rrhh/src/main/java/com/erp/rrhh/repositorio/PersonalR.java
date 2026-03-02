package com.erp.rrhh.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.Personal;

public interface PersonalR extends JpaRepository<Personal, Long> {
    boolean existsByIdentificacion(String identificacion);

    boolean existsByIdentificacionAndIdpersonalNot(String identificacion, Long idpersonal);

    Optional<Personal> findByIdpersonal(Long idpersonal);
}
