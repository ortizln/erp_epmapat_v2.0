package com.erp.sri.repository;

import java.math.BigDecimal;
import java.util.List;

import com.erp.sri.interfaces.Interes_int;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.sri.interfaces.Factura_int;
import com.erp.sri.model.Facturas;

public interface Facturas_rep extends JpaRepository<Facturas, Long> {
    //@Query(value = "SELECT * FROM facturas f join clientes c on f.idcliente = c.idcliente where f.pagado = 1", nativeQuery = true)
	@Query(value = """
            select
            	f.idfactura,
            	f.idmodulo,
            	SUM(case when f.swcondonar = true and rf.idrubro_rubros = 6 then 0 else rf.valorunitario * rf.cantidad end ) as total,
            	f.idcliente,
            	f.idabonado ,
            	f.feccrea,
            	f.formapago,
            	f.estado ,
            	f.pagado,
            	f.swcondonar,
            	f.fechacobro,
            	f.usuariocobro,
            	f.nrofactura,
            	c.nombre,
            	c.direccion,\s
            	m.descripcion as modulo,
            	COALESCE(t.interesapagar,0) as interes\s
            from
            	facturas f
            join rubroxfac rf on
            	f.idfactura = rf.idfactura_facturas
            join clientes c on
            	f.idcliente = c.idcliente
            join tmpinteresxfac t on\s
            	t.idfactura = f.idfactura\s
            join modulos m on 
                f.idmodulo = m.idmodulo
            where
            	f.totaltarifa > 0
            	and f.idcliente = ?1
            	and (( (f.estado = 1
            		or f.estado = 2)
            	and f.fechacobro is null)
            	or f.estado = 3 )
            	and f.fechaeliminacion is null
            	and fechaconvenio is null
            	and not rf.idrubro_rubros in (165,5) 
            group by
            	f.idfactura,
            	c.nombre,
            	c.direccion,\s
            	t.interesapagar,\s
            	m.descripcion
            order by
            	f.idabonado asc,
            	f.feccrea asc
            """, nativeQuery = true)
    public List<Factura_int> findSinCobrar(Long idcliente);
    @Query(value = """
    SELECT
        f.idfactura,
        f.idmodulo,
        COALESCE(rf_tot.total, 0) AS total,
        f.idcliente,
        f.idabonado,
        f.feccrea,
        f.formapago,
        f.estado,
        f.pagado,
        f.swcondonar,
        f.fechacobro,
        f.usuariocobro,
        f.nrofactura,
        c.nombre,
        a.direccionubicacion AS direccion,
        m.descripcion AS modulo,
        COALESCE(t_int.interesapagar, 0) AS interes
    FROM facturas f
    JOIN abonados a
        ON f.idabonado = a.idabonado
    JOIN clientes c
        ON a.idresponsable = c.idcliente
    /* total de rubros por factura (excluye 165 y 5) y considerando swcondonar=TRUE con rubro 6 */
    LEFT JOIN (
        SELECT
            rf.idfactura_facturas AS idfactura,
            SUM(
                CASE
                    WHEN f2.swcondonar = TRUE AND rf.idrubro_rubros = 6 THEN 0
                    ELSE rf.valorunitario * rf.cantidad
                END
            ) AS total
        FROM rubroxfac rf
        JOIN facturas f2
            ON f2.idfactura = rf.idfactura_facturas
        WHERE rf.idrubro_rubros NOT IN (165, 5)
        GROUP BY rf.idfactura_facturas
    ) rf_tot
        ON rf_tot.idfactura = f.idfactura
    /* intereses por factura (puede no existir) */
    LEFT JOIN (
        SELECT idfactura, SUM(interesapagar) AS interesapagar
        FROM tmpinteresxfac
        GROUP BY idfactura
    ) t_int
        ON t_int.idfactura = f.idfactura
    JOIN modulos m
        ON f.idmodulo = m.idmodulo
    WHERE
        f.totaltarifa > 0
        AND f.idabonado = ?1
        AND (
              ((f.estado = 1 OR f.estado = 2) AND f.fechacobro IS NULL)
              OR f.estado = 3
        )
        AND f.fechaeliminacion IS NULL
        AND f.fechaconvenio IS NULL
    ORDER BY
        f.idabonado ASC,
        f.feccrea ASC
    """, nativeQuery = true)
    List<Factura_int> findSinCobrarByCuenta(Long cuenta);

    @Query(value = """
SELECT
    f.idfactura,
    f.idmodulo,
    SUM(
        CASE
            WHEN f.swcondonar = TRUE AND rf.idrubro_rubros = 6 THEN 0
            ELSE rf.valorunitario * rf.cantidad
        END
    ) AS total,
    f.idcliente,
    f.idabonado,
    f.feccrea,
    f.formapago,
    f.estado,
    f.pagado,
    f.swcondonar,
    f.fechacobro,
    f.usuariocobro,
    f.nrofactura,
    c.nombre,
    c.direccion,
    m.descripcion AS modulo,
    COALESCE(MAX(t.interesapagar), 0) AS interes
FROM facturas f
JOIN rubroxfac rf
    ON f.idfactura = rf.idfactura_facturas
JOIN clientes c
    ON f.idcliente = c.idcliente
LEFT JOIN (
    SELECT idfactura, SUM(interesapagar) AS interesapagar
    FROM tmpinteresxfac
    GROUP BY idfactura
) t
    ON t.idfactura = f.idfactura
JOIN modulos m
    ON f.idmodulo = m.idmodulo
WHERE
    f.totaltarifa > 0
    AND f.idcliente = ?1
    AND NOT ( f.idmodulo = 4 OR (f.idmodulo = 3 AND f.idabonado > 0) )
    AND ( ( (f.estado = 1 OR f.estado = 2) AND f.fechacobro IS NULL ) OR f.estado = 3 )
    AND f.fechaeliminacion IS NULL
    AND f.fechaconvenio IS NULL
    AND rf.idrubro_rubros NOT IN (165, 5)
GROUP BY
    f.idfactura,
    f.idmodulo,
    f.idcliente,
    f.idabonado,
    f.feccrea,
    f.formapago,
    f.estado,
    f.pagado,
    f.swcondonar,
    f.fechacobro,
    f.usuariocobro,
    f.nrofactura,
    c.nombre,
    c.direccion,
    m.descripcion
ORDER BY
    f.idcliente ASC,
    f.feccrea ASC
    """, nativeQuery = true)
    List<Factura_int> findSincobroNotConsumo(Long idcliente);

    //Obtener la suma de los rubros para calcular el interes
    @Query(value = "select rf.idfactura_facturas as idfactura, sum(rf.cantidad * rf.valorunitario) as suma,f.feccrea, f.swcondonar from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas  where f.idfactura = ?1 and not ( rf.idrubro_rubros = 165 or rf.idrubro_rubros = 5) and not f.formapago = 4 group by rf.idfactura_facturas , f.feccrea, f.swcondonar ", nativeQuery = true)
    public List<Interes_int> getForIntereses(Long idfactura);
    @Query(value = "select  SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas where f.totaltarifa > 0 and f.idfactura= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165 ", nativeQuery = true)
    public BigDecimal getValorACobrar(Long idfactura);
}
