package com.erp.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.Fec_factura_pagos;

public interface Fec_factura_pagosR extends JpaRepository<Fec_factura_pagos, Long> {
    @Query(value = "select * from fec_factura_pagos where idfactura = ?1", nativeQuery = true)
    public List<Fec_factura_pagos> getByIdfactura(Long idfactura);

}
