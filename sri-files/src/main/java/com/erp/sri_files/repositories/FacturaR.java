package com.erp.sri_files.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.sri_files.models.Factura;
import org.springframework.data.jpa.repository.Query;

public interface FacturaR extends JpaRepository<Factura, Long>{
Factura findByIdfactura(Long idfactura);
}
