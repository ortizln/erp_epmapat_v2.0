package com.erp.comercializacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.erp.comercializacion.models.Novedad;

//@Repository
public interface NovedadR extends JpaRepository<Novedad, Long> {

   // Validación de Descripcion
   @Query(value = "SELECT * FROM novedades WHERE descripcion=?1", nativeQuery = true)
   List<Novedad> findByDescri(String descripcion);

   @Query(value = "SELECT * FROM novedades WHERE estado = ?1 ORDER BY idnovedad DESC", nativeQuery = true)
   List<Novedad> findByEstado(Long estado);

}
