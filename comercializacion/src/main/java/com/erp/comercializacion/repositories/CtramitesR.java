package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Ctramites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface CtramitesR extends JpaRepository<Ctramites, Long> {
    //Trámites por Tipo de Trámite
    @Query(value = "SELECT * FROM ctramites WHERE idtptramite_tptramite=?1 ORDER BY idctramite DESC LIMIT 20", nativeQuery = true)
    public List<Ctramites> findByTpTramite(Long idTpTramite);

    @Query(value = "SELECT * FROM ctramites WHERE LOWER(descripcion) LIKE %?1%", nativeQuery = true)
    public List<Ctramites> findByDescripcion(String descripcion);

    @Query(value = "SELECT * FROM ctramites WHERE feccrea=DATE(?1)", nativeQuery = true)
    public List<Ctramites> findByfeccrea(Date feccrea);
    //Trámites por Cliente
    @Query(value = "SELECT * FROM ctramites WHERE idcliente_clientes=?1 ORDER BY idctramite DESC", nativeQuery = true)
    public List<Ctramites> findByIdcliente(Long idcliente);
}
