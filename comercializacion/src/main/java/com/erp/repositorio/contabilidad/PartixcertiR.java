package com.erp.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.contabilidad.Partixcerti;

public interface PartixcertiR extends JpaRepository<Partixcerti, Long>{

	@Query(value = "SELECT * FROM partixcerti WHERE idcerti = ?1", nativeQuery = true)
	public List<Partixcerti> findByIdcerti(Long idcerti);

}
