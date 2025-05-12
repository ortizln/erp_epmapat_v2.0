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
	@Query(value = "select f.idfactura, f.idmodulo, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total, f.idcliente, f.idabonado , f.feccrea, f.formapago, f.estado , f.pagado, f.swcondonar, f.fechacobro, f.usuariocobro, f.nrofactura, c.nombre from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas join clientes c on f.idcliente = c.idcliente where f.totaltarifa > 0 and f.idcliente= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165  group by f.idfactura, c.nombre ORDER BY f.idabonado asc, f.feccrea asc", nativeQuery = true)
    public List<Factura_int> findSinCobrar(Long idcliente);
    @Query(value = "select f.idfactura, f.idmodulo, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total, f.idcliente, f.idabonado , f.feccrea, f.formapago, f.estado , f.pagado, f.swcondonar, f.fechacobro, f.usuariocobro, f.nrofactura, c.nombre from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas join abonados a on f.idabonado = a.idabonado join clientes c on a.idresponsable = c.idcliente where f.totaltarifa > 0 and f.idabonado= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165 group by f.idfactura, c.nombre ORDER BY f.idabonado asc, f.feccrea asc\n", nativeQuery = true)
    public List<Factura_int> findSinCobrarByCuenta(Long cuenta);
    @Query (value = "select f.idfactura, f.idmodulo, SUM(case when f.swcondonar = true and rf.idrubro_rubros = 6 then 0 else rf.valorunitario * rf.cantidad end ) as total, f.idcliente, f.idabonado, f.feccrea, f.formapago, f.estado, f.pagado, f.swcondonar, f.fechacobro, f.usuariocobro, f.nrofactura, c.nombre from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas join clientes c on f.idcliente = c.idcliente where f.totaltarifa > 0 and f.idcliente = ?1 and not ( f.idmodulo = 4 or (f.idmodulo = 3 and f.idabonado > 0)) and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165 group by f.idfactura, c.nombre , c.idcliente order by c.idcliente asc,f.feccrea asc", nativeQuery = true)
    public List<Factura_int> findSincobroNotConsumo(Long idcliente);
    //Obtener la suma de los rubros para calcular el interes
    @Query(value = "select rf.idfactura_facturas as idfactura, sum(rf.cantidad * rf.valorunitario) as suma,f.feccrea, f.swcondonar from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas  where f.idfactura = ?1 and not ( rf.idrubro_rubros = 165 or rf.idrubro_rubros = 5) and not f.formapago = 4 group by rf.idfactura_facturas , f.feccrea, f.swcondonar ", nativeQuery = true)
    public List<Interes_int> getForIntereses(Long idfactura);
    @Query(value = "select  SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas where f.totaltarifa > 0 and f.idfactura= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165 ", nativeQuery = true)
    public BigDecimal getValorACobrar(Long idfactura);
}
