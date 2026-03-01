package com.erp.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.ClienteMergeCliente;

public interface ClienteMergeClienteR extends JpaRepository<ClienteMergeCliente, Long> {
    List<ClienteMergeCliente> findByIdMerge(Long idMerge);
}
