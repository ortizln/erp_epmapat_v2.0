package com.erp.pagosonline.repositories;

import com.erp.pagosonline.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioR extends JpaRepository<Inventario, Long> {
}
