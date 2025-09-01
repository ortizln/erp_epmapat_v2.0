package com.erp.comercializacion
.repositories;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

import com.erp.comercializacion.models.ServiciosM;

// @Repository
public interface ServiciosR extends JpaRepository<ServiciosM, Long>{
	@Query(value = "SELECT * FROM servicios AS sv WHERE sv.swconsumo=?1 ORDER BY nombre", nativeQuery = true)
	public List<ServiciosM> findAll(Long q);
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM servicios AS s WHERE NOT EXISTS(SELECT * FROM servxabo AS sa WHERE sa.idservicio_servicios=s.idservicio)AND s.idservicio=?1 ", nativeQuery = true)
	void deleteByIdQ(Long id);
	@Query(value = "SELECT * FROM servicios AS s WHERE EXISTS(SELECT * FROM servxabo AS sa WHERE sa.idservicio_servicios=s.idservicio)AND s.idservicio=?1 ", nativeQuery = true)
	List<ServiciosM> used(Long id);

}
