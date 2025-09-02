package com.erp.comercializacion.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.comercializacion.models.Tmpinteresxfac;

public interface TmpinteresxfacR extends JpaRepository<Tmpinteresxfac, Long> {
    Tmpinteresxfac findByIdfactura(Long idfactura);
}
