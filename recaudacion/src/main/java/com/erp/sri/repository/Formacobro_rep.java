package com.erp.sri.repository;

import com.erp.sri.model.Formacobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface Formacobro_rep extends JpaRepository<Formacobro, Long> {
    @Query (value = "select * from formacobro where idformacobro = ?1 and estado = true", nativeQuery = true)
    Formacobro valFormaCobro(Long idformacobro);

}
