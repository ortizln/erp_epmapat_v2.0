package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Facturas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturasR extends JpaRepository<Facturas, Long> {
}
