package com.erp.comercializacion.repositories;

import com.erp.comercializacion.interfaces.CarteraVencidaRubros_int;
import com.erp.comercializacion.interfaces.RubroxfacI;
import com.erp.comercializacion.interfaces.RubroxfacIReport;
import com.erp.comercializacion.models.Rubroxfac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RubroxfacR extends JpaRepository<Rubroxfac, Long> {

    // Rubros de una Planilla por Nro. de Factura
    // @Query(value="SELECT * FROM rubroxfac WHERE idfactura_facturas=?1",
    // nativeQuery=true)
    // public List<Rubroxfac> findByFactura(Long nrofactura);

    // Campos específicos: Rubro y Valor de una Factura (Planilla)
    @Query(value = "SELECT new map(" +
            "r.descripcion as descripcion, " + "rf.valorunitario as valorunitario) " +
            "FROM Rubroxfac rf INNER JOIN Rubros r ON r.idrubro = rf.idrubro_rubros WHERE rf.idfactura_facturas=?1 order by rf.idrubro_rubros", nativeQuery = true)
    List<Map<String, Object>> rubrosByIdfactura(Long idfactura);

    @Query(value = "select sum(valorunitario)  from rubroxfac r where idfactura_facturas = ?1", nativeQuery = true)
    Double findSuma(Long idfactura);

    /* Obtener suma de rubros x factura */
    @Query(value = "select sum(valorunitario) from rubroxfac rf join rubros r on rf.idrubro_rubros = r.idrubro where idfactura_facturas = ?1", nativeQuery = true)
    Double sumaRubros(Long idfactura);

    /*
     * @Modifying
     *
     * @Query("DELETE FROM Rubroxfac r WHERE r.idrubroxfac = :idrubroxfac")
     * void deleteRubroDuplicado(@Param("idrubroxfac") Long idrubroxfac);
     */

    @Query(value = " select * from rubroxfac rf where rf.idfactura_facturas = ?1 and rf.idrubro_rubros = ?2", nativeQuery = true)
    List<Rubroxfac> getOneFxR(Long idfactura, Long idrubro);

    /*
     * @Query(value =
     * "select * from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where f.fechacobro = ?1 group by rf.idrubro_rubros"
     * , nativeQuery = true)
     */
    @Query(value = "select rf.idrubro_rubros , sum(rf.valorunitario * rf.cantidad) from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where f.fechacobro = ?1 group by rf.idrubro_rubros ", nativeQuery = true)
    List<RubroxfacI> getByFechaCobro(Date d, Date h);

    @Query(value = "SELECT * FROM rubroxfac rf JOIN facturas f ON rf.idfactura_facturas = f.idfactura WHERE f.fechacobro between ?1 and ?2", nativeQuery = true)
    public List<Rubroxfac> findByFecha(Date d, Date h);

    /* SIN COBRO 2.0 */
    @Query(value = "select * from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where totaltarifa > 0 and idcliente=?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaanulacion is null and f.fechaeliminacion is null ORDER BY f.idabonado, f.idfactura ", nativeQuery = true)
    public List<Rubroxfac> findSinCobroRF(Long cuenta);

    // Rubros de una Planilla
    @Query(value = "SELECT * FROM rubroxfac AS r WHERE r.idfactura_facturas=?1 order by idrubro_rubros", nativeQuery = true)
    public List<Rubroxfac> findByIdfactura(Long idfactura);

    // Rubros de una Planilla (Sin rubro 165 (Iva del siim 'esiva'))
    @Query(value = "SELECT * FROM rubroxfac AS r WHERE r.idfactura_facturas=?1 and idrubro_rubros <> 165 order by idrubro_rubros", nativeQuery = true)
    public List<Rubroxfac> findByIdfactura1(Long idfactura);

    // Rubroxfac de un Rubro (movimientos de un Rubro)
    @Query(value = "SELECT * FROM rubroxfac WHERE idrubro_rubros =?1 order by idrubroxfac desc limit 100", nativeQuery = true)
    public List<Rubroxfac> findByIdrubro(Long idrubro);

    // Rubro.descripcion y rubroxfac.valorunitario
    @Query(value = "SELECT r.descripcion, rf.valorunitario "
            + "FROM rubroxfac rf "
            + "INNER JOIN rubros r ON rf.idrubro_rubros = r.idrubro "
            + "WHERE rf.idfactura_facturas = :idFactura", nativeQuery = true)
    List<Object[]> findRubros(@Param("idFactura") Long idFactura);

    // Multa en Planilla
    @Query(value = "SELECT EXISTS (SELECT 1 FROM Rubroxfac WHERE idrubro_rubros = 6 and idfactura_facturas = ?1)", nativeQuery = true)
    boolean findMulta(Long idfactura);

    @Query("SELECT r.idrubro, r.descripcion, SUM(rf.cantidad * rf.valorunitario) AS total FROM Rubroxfac rf INNER JOIN rf.idrubro_rubros r GROUP BY r.idrubro, r.descripcion")
    List<Object[]> findRubroTotalByRubroxfac();

    // Recaudcion diaria - Total por Rubro (Todas)
    @Query(value = "SELECT r.idrubro, r.descripcion, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , r.swiva AS iva FROM Rubroxfac rf "
            +
            "INNER JOIN rf.idrubro_rubros r " +
            "INNER JOIN rf.idfactura_facturas f " +
            "WHERE f.fechacobro = :fechacobro and not r.idrubro = 165 GROUP BY r.idrubro, r.descripcion", nativeQuery = true)
    List<Object[]> findRubroTotalByRubroxfacAndFechacobro(@Param("fechacobro") LocalDate fechacobro);

    // Recaudcion diaria - Total por Rubros A.A. (Desde Facturas)
    @Query(value = "SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , r.swiva AS iva FROM Rubroxfac rf "
            +
            "JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
            "JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
            "WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea <= ?3 AND NOT f.estado = 3 AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL) "
            +
            "GROUP BY r.descripcion, r.idrubro ORDER BY r.idrubro", nativeQuery = true)
    List<Object[]> totalRubrosAnteriorRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta);

    // Recaudcion diaria - Total por Rubro Año actual (Desde Facturas)
    @Query(value = "SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , r.swiva AS iva "
            +
            "FROM Rubroxfac rf " +
            "JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
            "JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
            "WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea > ?3 AND NOT f.estado = 3 AND f.fechaeliminacion IS NULL  "
            +
            "GROUP BY r.descripcion, r.idrubro " +
            "ORDER BY r.idrubro", nativeQuery = true)
    List<Object[]> totalRubrosActualRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta);

    // Recaudcion diaria - Total por Rubros A.A. (Desde Facturas)
    @Query(value = "SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , r.swiva AS iva FROM Rubroxfac rf "
            +
            "JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
            "JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
            "WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea <= ?3 AND NOT f.estado = 3 AND f.usuariocobro = ?4 AND f.fechaeliminacion IS NULL "
            +
            "GROUP BY r.descripcion, r.idrubro ORDER BY r.idrubro", nativeQuery = true)
    List<Object[]> totalRubrosAnteriorByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta, Long idrec);

    // Recaudcion diaria - Total por Rubro Año actual (Desde Facturas)
    @Query(value = "SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , r.swiva AS iva "
            +
            "FROM Rubroxfac rf " +
            "JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
            "JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
            "WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea > ?3 AND NOT f.estado = 3 AND f.usuariocobro = ?4 AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL)"
            +
            "GROUP BY r.descripcion, r.idrubro " +
            "ORDER BY r.idrubro", nativeQuery = true)
    List<Object[]> totalRubrosActualByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta, Long idrec);

    // Recaudcion diaria - Total por Rubros A.A. (Desde Facturas)
    @Query(value = "SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , r.swiva AS iva  FROM Rubroxfac rf "
            +
            "JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
            "JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
            "WHERE date(f.fechacobro) = ?1 AND f.feccrea <= ?2 AND (f.estado=1 OR f.estado=2) AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL) "
            +
            "GROUP BY r.descripcion, r.idrubro ORDER BY r.idrubro", nativeQuery = true)
    List<Object[]> totalRubrosAnterior(LocalDate fecha, LocalDate hasta);

    // Recaudcion diaria - Total por Rubro Año actual (Desde Facturas)
    @Query(value = "SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , r.swiva AS iva "
            +
            "FROM Rubroxfac rf " +
            "JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
            "JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
            "WHERE date(f.fechacobro) = ?1 AND f.feccrea > ?2 AND (f.estado=1 OR f.estado=2) AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL) "
            +
            "GROUP BY r.descripcion, r.idrubro " +
            "ORDER BY r.idrubro", nativeQuery = true)
    List<Object[]> totalRubrosActual(LocalDate fecha, LocalDate hasta);

    @Query(value = "select f.idfactura , sum((rf.cantidad * rf.valorunitario)*?1) as iva from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join rubros r on rf.idrubro_rubros = r.idrubro where idfactura = ?2 and r.swiva = true group by f.idfactura", nativeQuery = true)
    List<Object[]> getIva(BigDecimal iva, Long idfactura);

    /* FACTURACION ELECTRONICA */

    // @Query(value = "select rf from rubroxfac rf join facturas f on
    // rf.idfactura_facturas = f.idfactura join rubros r on rf.idrubro_rubros =
    // r.idrubro where f.idfactura = ?1 and not r.idrubro = 165 and f.pagado = 1",
    // nativeQuery = true)
    @Query(value = "select * from rubroxfac rf where rf.idfactura_facturas = ?1 and not rf.idrubro_rubros = 165 order by idrubro_rubros asc", nativeQuery = true)
    List<Rubroxfac> getRubrosByFactura(Long idfactura);

    @Query(value = "select rf.idrubro_rubros, r.descripcion, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas join rubros r on rf.idrubro_rubros = r.idrubro where f.idabonado > 0 and (f.idmodulo = 3 or f.idmodulo = 4 )and f.totaltarifa > 0 and f.idabonado = ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165 group by rf.idrubro_rubros, r.descripcion", nativeQuery = true)
    List<RubroxfacIReport> getRubrosByAbonado(Long idabonado);

    /* FIND MULTAS BY ID FACUTA */
    @Query(value = "select * from rubroxfac where idfactura_facturas = ?1 and idrubro_rubros = 6", nativeQuery = true)
    List<Rubroxfac> getMultaByIdFactura(Long idfactura);

    /* REPORTE DE CARTERA VENCIDA POR RUBROS */
    @Query(value = "select r.idrubro as codigo, r.descripcion, count(f.idfactura) facturas, sum(rf.cantidad * rf.valorunitario) as total from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join rubros r on rf.idrubro_rubros = r.idrubro"
            +
            " where f.totaltarifa > 0 and (( (f.estado = 1 or f.estado = 2) and ( f.fechacobro > ?1 or f.fechacobro is null)) or f.estado = 3 )"
            +
            " and f.fechaconvenio is null and f.fechaeliminacion is null group by rf.idrubro_rubros, r.descripcion, r.idrubro ", nativeQuery = true)
    List<CarteraVencidaRubros_int> getCarteraVencidaxRubros(Date fechacobro);

    @Query(value = "SELECT sum(rf.cantidad * rf.valorunitario) as interes FROM rubroxfac rf where rf.idfactura_facturas = ?1 and rf.idrubro_rubros = 5", nativeQuery = true)
    BigDecimal getTotalInteres(Long idfactura);
    /* CONSULTA PARA REMISIONES */

    @Query(value = "select \r\n" + //
            "r.descripcion,\r\n" + //
            "sum(rf.cantidad * rf.valorunitario) as sum\t\r\n" + //
            "from\r\n" + //
            "\trubroxfac rf\r\n" + //
            "join facturas f on\r\n" + //
            "\trf.idfactura_facturas = f.idfactura\r\n" + //
            "join rubros r on \r\n" + //
            "\trf.idrubro_rubros = r.idrubro \r\n" + //
            "join modulos m on\r\n" + //
            "\tf.idmodulo = m.idmodulo\r\n" + //
            "join clientes c on\r\n" + //
            "\tf.idcliente = c.idcliente\r\n" + //
            "where\r\n" + //
            "\tf.totaltarifa > 0\r\n" + //
            "\tand f.idcliente = ?1\r\n" + //
            "\tand (( (f.estado = 1\r\n" + //
            "\t\tor f.estado = 2)\r\n" + //
            "\tand f.fechacobro is null)\r\n" + //
            "\tor f.estado = 3 )\r\n" + //
            "\tand f.fechaeliminacion is null\r\n" + //
            "\tand f.fechaconvenio is null\r\n" + //
            "\tand not rf.idrubro_rubros = 165\r\n" + //
            "\tand f.feccrea <= ?2\r\n" + //
            "\tand (f.idmodulo = 3\r\n" + //
            "\t\tor f.idmodulo = 4\r\n" + //
            "\t\tor f.idmodulo = 27) \r\n" + //
            "\t\t\r\n" + //
            "\t\tgroup by r.descripcion\r\n" + //
            "", nativeQuery = true)
    public List<RubroxfacI> getRubrosForRemisiones(Long idcliente, LocalDate topefecha);

}
