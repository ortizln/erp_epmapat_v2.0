package com.erp.comercializacion.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.comercializacion.models.CtramitesM;

public interface CtramitesR extends JpaRepository<CtramitesM, Long> {
	// Trámites por Tipo de Trámite
	@Query(value = "SELECT * FROM ctramites WHERE idtptramite_tptramite=?1 ORDER BY idctramite DESC LIMIT 20", nativeQuery = true)
	public List<CtramitesM> findByTpTramite(Long idTpTramite);

	@Query(value = "SELECT * FROM ctramites WHERE LOWER(descripcion) LIKE %?1%", nativeQuery = true)
	public List<CtramitesM> findByDescripcion(String descripcion);

	@Query(value = "SELECT * FROM ctramites WHERE feccrea=DATE(?1)", nativeQuery = true)
	public List<CtramitesM> findByfeccrea(Date feccrea);

	// Trámites por Cliente
	@Query(value = "SELECT * FROM ctramites WHERE idcliente_clientes=?1 ORDER BY idctramite DESC", nativeQuery = true)
	public List<CtramitesM> findByIdcliente(Long idcliente);

}
