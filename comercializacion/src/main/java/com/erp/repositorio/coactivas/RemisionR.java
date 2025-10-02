package com.erp.repositorio.coactivas;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.coactivas.Remision;

public interface RemisionR extends JpaRepository<Remision, Long>{
@Query(value = "SELECT * FROM remision r where r.feccrea BETWEEN ?1 and ?2", nativeQuery = true)
public List<Remision> findRemisionesByFeccrea(LocalDate d,  LocalDate h);
    
}
