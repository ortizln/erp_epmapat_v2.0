package com.erp.comercializacion
.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.comercializacion.models.AboxSuspensionM;

public interface AboxSuspensionR extends JpaRepository<AboxSuspensionM, Long> {

	@Query(value = "SELECT * FROM aboxsuspension WHERE idsuspension_suspensiones=?1", nativeQuery = true)
	public List<AboxSuspensionM> findByIdsuspension(Long idsuspension);

}
