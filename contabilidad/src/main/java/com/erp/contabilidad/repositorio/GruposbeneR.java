package com.erp.contabilidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.contabilidad.modelo.Gruposbene;

public interface GruposbeneR extends JpaRepository<Gruposbene, Long>{

   
   @Query(value = "SELECT * FROM gruposbene order by nomgru", nativeQuery=true)
	List<Gruposbene> findAll();

   @Query(value = "SELECT * FROM gruposbene where LOWER(nomgru)=?1", nativeQuery=true)
	List<Gruposbene> findByNomgru(String nomgru);

   Gruposbene findByIdgrupo(Long idgrupo);   //Un registro
   
}

