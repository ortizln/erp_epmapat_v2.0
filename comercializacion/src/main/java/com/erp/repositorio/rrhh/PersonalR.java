package com.erp.repositorio.rrhh;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.rrhh.Personal;

public interface PersonalR extends JpaRepository<Personal, Long> {
    
}
