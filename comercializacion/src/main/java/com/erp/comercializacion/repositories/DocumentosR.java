package com.erp.comercializacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.comercializacion.models.Documentos;


public interface DocumentosR extends JpaRepository<Documentos, Long>{

   @Query(value = "SELECT * FROM documentos order by nomdoc", nativeQuery=true)
	List<Documentos> findAll();

   @Query(value = "SELECT * FROM documentos where LOWER(nomdoc)=?1", nativeQuery=true)
	List<Documentos> findByNomdoc(String nomdoc);

}
