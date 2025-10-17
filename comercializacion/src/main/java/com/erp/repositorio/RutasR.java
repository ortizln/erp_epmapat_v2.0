package com.erp.repositorio;

import java.util.List;

import com.erp.interfaces.RutasInterfaces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.interfaces.CuentasByRutas;
import com.erp.modelo.Rutas;

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

    @Query(value = """
            SELECT
                rt.descripcion AS ruta,
                a.idabonado AS cuenta,
                c.nombre,
                c.cedula,
                a.direccionubicacion,
                SUM(rf.cantidad * rf.valorunitario) AS subtotal,
                COALESCE(SUM(ti.interesapagar), 0) AS total_interes,
                SUM(rf.cantidad * rf.valorunitario) + COALESCE(SUM(ti.interesapagar), 0) AS total_final,
                COUNT(DISTINCT f.idfactura) AS total_facturas
            FROM facturas f
            JOIN rubroxfac rf ON f.idfactura = rf.idfactura_facturas
            JOIN clientes c ON c.idcliente = f.idcliente
            JOIN abonados a ON a.idabonado = f.idabonado
            JOIN rutas rt ON a.idruta_rutas = rt.idruta
            LEFT JOIN tmpinteresxfac ti ON f.idfactura = ti.idfactura
            WHERE
                ( (f.estado IN (1, 2) AND f.fechacobro IS NULL) OR f.estado = 3 )
                AND f.fechaconvenio IS NULL
                AND f.fechaeliminacion IS NULL
                AND rt.idruta = ?1
            GROUP BY
                rt.descripcion,
                a.idabonado,
                c.nombre,
                c.cedula,
                a.direccionubicacion
            ORDER BY
                rt.descripcion,
                a.idabonado;
            """, nativeQuery = true)
    public List<RutasInterfaces>getDeudaOfCuentasByIdrutas(Long idruta);
    @Query(value = """
            SELECT
                rt.descripcion AS ruta,
                a.idabonado AS cuenta,
                c.nombre,
                c.cedula,
                a.direccionubicacion,
                SUM(rf.cantidad * rf.valorunitario) AS subtotal,
                COALESCE(SUM(ti.interesapagar), 0) AS total_interes,
                SUM(rf.cantidad * rf.valorunitario) + COALESCE(SUM(ti.interesapagar), 0) AS total_final,
                COUNT(DISTINCT f.idfactura) AS total_facturas
            FROM facturas f
            JOIN rubroxfac rf ON f.idfactura = rf.idfactura_facturas
            JOIN clientes c ON c.idcliente = f.idcliente
            JOIN abonados a ON a.idabonado = f.idabonado
            JOIN rutas rt ON a.idruta_rutas = rt.idruta
            LEFT JOIN tmpinteresxfac ti ON f.idfactura = ti.idfactura
            WHERE
                ( (f.estado IN (1, 2) AND f.fechacobro IS NULL) OR f.estado = 3 )
                AND f.fechaconvenio IS NULL
                AND f.fechaeliminacion IS NULL
            GROUP BY
                rt.descripcion,
                a.idabonado,
                c.nombre,
                c.cedula,
                a.direccionubicacion
            ORDER BY
                rt.descripcion,
                a.idabonado;
            """, nativeQuery = true)
    public List<RutasInterfaces> getDeudasOfAllCuentas();

    @Query(value = """
            SELECT
                rt.idruta,
                rt.descripcion AS ruta,
                COUNT(DISTINCT a.idabonado) AS cuenta,
                SUM(rf.cantidad * rf.valorunitario) AS subtotal,
                COALESCE(SUM(ti.interesapagar), 0) AS total_interes,
                SUM(rf.cantidad * rf.valorunitario) + COALESCE(SUM(ti.interesapagar), 0) AS total_final
            FROM facturas f
            JOIN rubroxfac rf ON f.idfactura = rf.idfactura_facturas
            JOIN abonados a ON a.idabonado = f.idabonado
            JOIN rutas rt ON a.idruta_rutas = rt.idruta
            LEFT JOIN tmpinteresxfac ti ON f.idfactura = ti.idfactura
            WHERE
                ( (f.estado IN (1, 2) AND f.fechacobro IS NULL) OR f.estado = 3 )
                AND f.fechaconvenio IS NULL
                AND f.fechaeliminacion IS NULL
            GROUP BY
                rt.idruta,
                rt.descripcion
            ORDER BY
                rt.idruta;
          
            """, nativeQuery = true)
    public List<RutasInterfaces> getDeudasOfRutas();
}
