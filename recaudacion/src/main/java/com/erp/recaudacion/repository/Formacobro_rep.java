package com.erp.recaudacion.repository;

import com.erp.recaudacion.model.Formacobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Formacobro_rep extends JpaRepository<Formacobro, Long> {
    @Query (value = "select * from formacobro where idformacobro = ?1 and estado = true", nativeQuery = true)
    Formacobro valFormaCobro(Long idformacobro);

}
