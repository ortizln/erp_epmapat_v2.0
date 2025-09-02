package com.erp.comercializacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.comercializacion.models.LiquidaTramite;

public interface LiquidaTramiteR extends JpaRepository<LiquidaTramite, Long> {

	@Query(value = "SELECT * FROM liquidatrami WHERE idtramite_tramites=?1", nativeQuery = true)
	public List<LiquidaTramite> findByIdTramite(Long idtramite);

}
