package com.erp.pagosonline.repositories;

import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.models.Facturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacturasR extends JpaRepository<Facturas, Long> {
    @Query(value = "select f.idfactura, sum(rf.cantidad * rf.valorunitario) as subtotal, f.formapago, f.feccrea, f.fechatransferencia, c.nombre from facturas f join clientes c on f.idcliente = c.idcliente join rubroxfac rf on f.idfactura = rf.idfactura_facturas where f.idabonado = ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaeliminacion is null group by f.idfactura, c.nombre ORDER BY f.idfactura", nativeQuery = true)
    public List<FacturasSinCobroInter> findFacturasSinCobro(Long cuenta);

}
