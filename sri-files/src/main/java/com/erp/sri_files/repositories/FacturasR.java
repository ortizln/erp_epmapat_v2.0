package com.erp.sri_files.repositories;

import com.erp.sri_files.models.Facturas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FacturasR extends JpaRepository<Facturas, Long> {
    Facturas findByIdfactura(Long idfactura);

}
