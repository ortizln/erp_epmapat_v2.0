package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Servicios1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Servicios1R extends JpaRepository<Servicios1, Long> {
    @Query(value="SELECT * FROM servicios1 AS s WHERE idmodulo_modulos=?1",nativeQuery = true)
    public List<Servicios1> findByIdModulos(Long idmodulo);
}
