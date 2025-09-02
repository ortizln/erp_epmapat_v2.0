package com.erp.repositorio.administracion;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.administracion.Definir;

public interface DefinirR extends JpaRepository<Definir, Long> {
    Definir findTopByOrderByIddefinirDesc();

}
