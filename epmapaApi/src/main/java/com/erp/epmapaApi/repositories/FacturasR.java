package com.erp.epmapaApi.repositories;

import com.erp.epmapaApi.DTO.FacturasSinCobroInter;
import com.erp.epmapaApi.models.Facturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacturasR extends JpaRepository<Facturas, Long> {
    @Query(value = "select f.idfactura, sum(rf.cantidad * rf.valorunitario) as subtotal, c.nombre, c.cedula, a.idabonado as cuenta, a.direccionubicacion, f.formapago, e.feccrea, f.fechatransferencia as fectransferencia "
            +
            "from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas " +
            "join clientes c on c.idcliente = f.idcliente " +
            "join lecturas l on l.idfactura = f.idfactura " +
            "join emisiones e on l.idemision = e.idemision " +
            "join abonados a on a.idabonado = f.idabonado " +
            "where f.idabonado = ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaeliminacion is null "
            +
            "group by f.idfactura, c.nombre, c.cedula, a.idabonado, a.direccionubicacion, e.feccrea ORDER BY f.idfactura", nativeQuery = true)
    public List<FacturasSinCobroInter> findSincobroDatos(Long cuenta);
}
