package com.erp.login.repositories;

import com.erp.login.models.Documentos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DocumentosR extends JpaRepository<Documentos, Long> {
    @Query(value = "SELECT * FROM documentos order by nomdoc", nativeQuery=true)
    List<Documentos> findAll();

    @Query(value = "SELECT * FROM documentos where LOWER(nomdoc)=?1", nativeQuery=true)
    List<Documentos> findByNomdoc(String nomdoc);
}
