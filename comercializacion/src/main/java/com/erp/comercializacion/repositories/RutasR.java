package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Rutas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RutasR extends JpaRepository<Rutas, Long> {
    @Query("SELECT COUNT(r) > 0 FROM Rutas r WHERE r.codigo = :codigo")
    boolean valCodigo(@Param("codigo") String codigo);

    @Query(value = "SELECT * FROM rutas WHERE estado = true ORDER BY idruta asc", nativeQuery = true)
    List<Rutas> findAllActive();
}
