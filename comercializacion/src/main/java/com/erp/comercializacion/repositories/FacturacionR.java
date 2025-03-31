package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Facturacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturacionR extends JpaRepository<Facturacion, Long> {
}
