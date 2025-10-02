package com.erp.login.repositories;

import com.erp.login.models.Tabla4;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Tabla4R extends JpaRepository<Tabla4, Long> {
    @Query(value = "SELECT * FROM tabla4 order by tipocomprobante", nativeQuery=true)
    List<Tabla4> findAll();

    @Query(value = "SELECT * FROM tabla4 where tipocomprobante=?1", nativeQuery=true)
    List<Tabla4> findByTipocomprobante(String tipocomprobante);

    @Query(value = "SELECT * FROM tabla4 where LOWER(nomcomprobante)=?1", nativeQuery=true)
    List<Tabla4> findByNomcomprobante(String nomcomprobante);

}
