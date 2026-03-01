package com.erp.rrhh.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.rrhh.modelo.Personal;

public interface PersonalR extends JpaRepository<Personal, Long> {
    
}

