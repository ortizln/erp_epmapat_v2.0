package com.erp.comercializacion.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.comercializacion.interfaces.CuentasByRutas;
import com.erp.comercializacion.models.Rutas;

// @Repository
public interface RutasR extends JpaRepository<Rutas, Long> {

	// @Query(value = "SELECT * FROM rutas where codigo=?1", nativeQuery=true)
	// List<Rutas> findByCodigo(String codigo);

	// Valida Código de la Ruta
	@Query("SELECT COUNT(r) > 0 FROM Rutas r WHERE r.codigo = :codigo")
	boolean valCodigo(@Param("codigo") String codigo);

	@Query(value = "SELECT * FROM rutas WHERE estado = true ORDER BY idruta asc", nativeQuery = true)
	List<Rutas> findAllActive();

	@Query(value = """
				select
				r.idruta,
				r.descripcion,
				r.codigo ,
				count(a) as ncuentas
			from
				rutas r
			join abonados a on
				a.idruta_rutas = r.idruta
			group by
				r.idruta
			order by
				r.idruta asc
			""", nativeQuery = true)
	public List<CuentasByRutas> getNcuentasByRutas();
}
