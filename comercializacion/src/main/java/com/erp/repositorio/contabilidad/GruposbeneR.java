package com.erp.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.contabilidad.Gruposbene;

public interface GruposbeneR extends JpaRepository<Gruposbene, Long>{

   
   @Query(value = "SELECT * FROM gruposbene order by nomgru", nativeQuery=true)
	List<Gruposbene> findAll();

   @Query(value = "SELECT * FROM gruposbene where LOWER(nomgru)=?1", nativeQuery=true)
	List<Gruposbene> findByNomgru(String nomgru);

   Gruposbene findByIdgrupo(Long idgrupo);   //Un registro
   
}
