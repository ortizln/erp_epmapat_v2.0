package com.erp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.Tmpinteresxfac;

public interface TmpinteresxfacR extends JpaRepository<Tmpinteresxfac, Long> {
    Tmpinteresxfac findByIdfactura(Long idfactura);
}
