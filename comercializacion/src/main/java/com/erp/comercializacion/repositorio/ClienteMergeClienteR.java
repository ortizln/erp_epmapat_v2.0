package com.erp.comercializacion.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.comercializacion.modelo.ClienteMergeCliente;

public interface ClienteMergeClienteR extends JpaRepository<ClienteMergeCliente, Long> {
    List<ClienteMergeCliente> findByIdMerge(Long idMerge);
}


