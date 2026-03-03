package com.erp.gestiondocumental.repositories;

import com.erp.gestiondocumental.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioR extends JpaRepository<Inventario, Long> {
}

