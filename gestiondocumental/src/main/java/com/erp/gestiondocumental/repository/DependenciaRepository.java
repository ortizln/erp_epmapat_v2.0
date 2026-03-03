package com.erp.gestiondocumental.repository;

import com.erp.gestiondocumental.model.Dependencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DependenciaRepository extends JpaRepository<Dependencia, UUID> {
    List<Dependencia> findByEntidadIdOrderByCodigo(UUID entidadId);
}
