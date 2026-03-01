package com.erp.comercializacion.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.comercializacion.modelo.ClienteMergeFactura;

public interface ClienteMergeFacturaR extends JpaRepository<ClienteMergeFactura, Long> {
    List<ClienteMergeFactura> findByIdMerge(Long idMerge);
    List<ClienteMergeFactura> findByFacturaId(Long facturaId);
}


