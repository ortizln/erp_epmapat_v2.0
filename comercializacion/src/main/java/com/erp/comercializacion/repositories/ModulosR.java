package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Modulos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModulosR extends JpaRepository<Modulos, Long> {
    List<Modulos> findByOrderByDescripcionAsc();

    @Query(value="SELECT * FROM modulos WHERE idmodulo = 23 OR idmodulo = 50", nativeQuery = true)
    public List<Modulos> getTwoModulos();

}
