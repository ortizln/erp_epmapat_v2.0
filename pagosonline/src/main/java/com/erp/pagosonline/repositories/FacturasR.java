package com.erp.pagosonline.repositories;

import com.erp.pagosonline.interfaces.FacturasCobradas;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.models.Facturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface FacturasR extends JpaRepository<Facturas, Long> {
    @Query(value = """
            select
                f.idfactura,
                sum(rf.cantidad * rf.valorunitario) as subtotal,
                f.formapago,
                f.feccrea,
                f.fechatransferencia,
                c.nombre,
                coalesce(ti.interesapagar, 0) as interes
            from facturas f
            join abonados a\s
                on f.idabonado = a.idabonado
            join clientes c\s
                on a.idresponsable = c.idcliente
            join rubroxfac rf\s
                on f.idfactura = rf.idfactura_facturas
            left join tmpinteresxfac ti\s
                on ti.idfactura = f.idfactura
            where
                f.idabonado = ?1
                and (((f.estado = 1 or f.estado = 2) and f.fechacobro is null)
                     or f.estado = 3)
                and f.fechaconvenio is null
                and f.fechaeliminacion is null
            group by
                f.idfactura,
                f.formapago,
                f.feccrea,
                f.fechatransferencia,
                c.nombre,
                ti.interesapagar
            order by
                f.idfactura;
            """, nativeQuery = true)
    public List<FacturasSinCobroInter> findFacturasSinCobro(Long cuenta);

    @Query(value = """
            select
            	count (rf.idrubro_rubros) as nrubros,
            	sum(rf.cantidad * rf.valorunitario) valortotal,
            	f.idfactura as planilla,
            	f.nrofactura,
            	f.fechacobro ,
            	f.horacobro,\s
            	u.nomusu as usuario
            from
            	facturas f
            join rubroxfac rf on
            	f.idfactura = rf.idfactura_facturas
            join usuarios u on\s
            	f.usuariocobro = u.idusuario
            where
            	f.usuariocobro = ?1
            	and f.fechacobro between ?2 and ?3
            	and f.horacobro between ?4 and ?5
            group by
            	f.idfactura,\s
            	u.nomusu\s
            """, nativeQuery = true)
    public List<FacturasCobradas> getReporteFacturasCobradas(Long idusuario, LocalDate df, LocalDate hf, LocalTime dh, LocalTime hh);

}
