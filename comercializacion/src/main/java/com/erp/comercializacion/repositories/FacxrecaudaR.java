package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Facxrecauda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface FacxrecaudaR extends JpaRepository<Facxrecauda, Long> {
    @Query(value = "select * from facxrecauda fr join recaudacion r on fr.idrecaudacion = r.idrecaudacion where r.recaudador = ?1 and (Date(r.fechacobro) BETWEEN ?2 AND ?3) order by r.fechacobro asc", nativeQuery = true)
    List<Facxrecauda> getByUsuFecha(Long idusuario, Date d, Date h);

    @Query(value = "SELECT * FROM facxrecauda where idfactura = ?1", nativeQuery = true)
    Facxrecauda getyByIdFactura(Long idfactura);

}
