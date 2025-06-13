package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Precioxcat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PrecioxcatR extends JpaRepository<Precioxcat, Long> {
    @Query(value = "SELECT * FROM precioxcat LIMIT 10 ", nativeQuery = true)
    public List<Precioxcat> findAll();

    @Query(value = "SELECT * FROM precioxcat AS p WHERE p.idcategoria_categorias=?1  AND p.m3 BETWEEN ?2 and ?3 ORDER by m3", nativeQuery = true)
    public List<Precioxcat> findAll(Long idcategoria_categorias, Long dm3, Long hm3);

    @Query(value = "SELECT * FROM precioxcat AS p WHERE p.idcategoria_categorias=?1 AND p.m3=?2", nativeQuery = true)
    public List<Precioxcat> findConsumo(Long idcategoria, Long m3 );
}
