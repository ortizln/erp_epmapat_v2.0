package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Facxconvenio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacxconvenioR extends JpaRepository<Facxconvenio, Long> {
    @Query(value = "SELECT * FROM facxconvenio LIMIT 10", nativeQuery = true)
    public List<Facxconvenio> find10();

    @Query(value = "SELECT * FROM facxconvenio WHERE idconvenio_convenios=?1", nativeQuery = true)
    List<Facxconvenio> findByConvenio(Long idconvenio);
}
