package com.erp.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.erp.modelo.Tmpinteresxfac;

import java.util.Collection;
import java.util.List;

public interface TmpinteresxfacR extends JpaRepository<Tmpinteresxfac, Long> {
    Tmpinteresxfac findByIdfactura(Long idfactura);
    List<Tmpinteresxfac> findAllByIdfacturaIn(Collection<Long> ids);

}
