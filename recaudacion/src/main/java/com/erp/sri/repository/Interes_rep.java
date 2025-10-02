package com.erp.sri.repository;

import com.erp.sri.model.Intereses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Interes_rep extends JpaRepository<Intereses,Long> {
    @Query(value = "SELECT i.porcentaje from intereses i where i.anio = ?1 and i.mes between ?2 and ?3 ", nativeQuery = true)
    List<Float> porcentajes(int year, int monthValue, int monthValue2);
}
