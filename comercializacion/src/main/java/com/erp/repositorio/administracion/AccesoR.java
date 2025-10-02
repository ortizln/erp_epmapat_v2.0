package com.erp.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.administracion.Acceso;

public interface AccesoR extends JpaRepository<Acceso, Long>{
   
   @Query(value = "SELECT * FROM acceso order by codacc", nativeQuery = true)
   List<Acceso> findAll();
   
}
