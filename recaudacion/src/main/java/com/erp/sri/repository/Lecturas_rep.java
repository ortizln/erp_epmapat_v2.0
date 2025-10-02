package com.erp.sri.repository;

import com.erp.sri.interfaces.Interes_int;
import com.erp.sri.model.Lecturas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface Lecturas_rep extends JpaRepository<Lecturas, Long> {
    @Query(value = "select rf.idfactura_facturas as idfactura, sum(rf.cantidad * rf.valorunitario) as suma, e.feccrea, f.swcondonar from lecturas l join rubroxfac rf on l.idfactura = rf.idfactura_facturas join emisiones e on l.idemision = e.idemision join facturas f on l.idfactura = f.idfactura where l.idfactura = ?1 and not (rf.idrubro_rubros = 165 or rf.idrubro_rubros = 5) and not f.formapago = 4  group by rf.idfactura_facturas, e.feccrea,  f.swcondonar", nativeQuery = true)
    public List<Interes_int> getForIntereses(Long idfactura);
}
