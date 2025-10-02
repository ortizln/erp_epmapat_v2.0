package com.erp.pagosonline.repositories;

import com.erp.pagosonline.models.Formacobro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FormacobroR extends JpaRepository<Formacobro, Long> {
    @Query(value = "select * from formacobro where idformacobro = ?1 and estado = true", nativeQuery = true)
    Formacobro valFormaCobro(Long idformacobro);
}
