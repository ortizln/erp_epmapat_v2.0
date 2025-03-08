package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Novedades;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NovedadesR extends JpaRepository<Novedades, Long> {
    //Validación de Descripcion
    @Query(value = "SELECT * FROM novedades WHERE descripcion=?1", nativeQuery = true)
    List<Novedades> findByDescri(String descripcion);
    @Query(value = "SELECT * FROM novedades WHERE estado = ?1 ORDER BY idnovedad DESC", nativeQuery = true)
    List<Novedades> findByEstado(Long estado);
}
