package com.erp.sri.repository;

import com.erp.sri.interfaces.Rubroxfaciva_int;
import com.erp.sri.model.Rubroxfac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Rubroxfac_rep extends JpaRepository<Rubroxfac, Long> {
    @Query(value = "select rf.cantidad , rf.valorunitario, r.swiva as iva from rubroxfac rf join rubros r on rf.idrubro_rubros = r.idrubro where rf.idfactura_facturas = ?1", nativeQuery = true)
    List<Rubroxfaciva_int> getRubrosByIdFactura(Long idfactura);
}
