package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Itemxfact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemxfactR extends JpaRepository<Itemxfact, Long> {
    //Productos de una Facturación (itemxfac de una Facturacion)
    @Query(value = "SELECT * FROM itemxfact AS i WHERE i.idfacturacion_facturacion=?1", nativeQuery=true)
    public List<Itemxfact> findByIdfacturacion(Long idfacturacion);

    //Movimiento de un Prodcto (itemxfact de una Catalogoitems)
    @Query(value = "SELECT * FROM itemxfact where idcatalogoitems_catalogoitems=?1 order by iditemxfact DESC LIMIT 10", nativeQuery = true)
    public List<Itemxfact> findByIdcatalogoitems( Long Idcatalogoitems);
}
