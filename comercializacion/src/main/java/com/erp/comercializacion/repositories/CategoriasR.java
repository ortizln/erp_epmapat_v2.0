package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Categorias;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface CategoriasR extends JpaRepository<Categorias, Long> {
    // Categorias habilitadas
    @Query(value = "SELECT c.descripcion FROM Categorias c WHERE c.habilitado = true ORDER BY c.codigo ASC")
    List<String> listaCategorias();

    @Query(value = "SELECT * FROM categorias AS c WHERE EXISTS(SELECT * FROM abonados AS a , precioxcat as p WHERE a.idcategoria_categorias=c.idcategoria AND p.idcategoria_categorias=c.idcategoria)AND c.idcategoria=?1 ", nativeQuery = true)
    List<Categorias> used(Long id);

    // Validación Nombre de la Categoria
    @Query(value = "SELECT * FROM categorias AS c WHERE c.descripcion=?1", nativeQuery = true)
    List<Categorias> findByDescri(String descripcion);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM categorias AS c WHERE NOT EXISTS(SELECT * FROM abonados AS a , precioxcat as p WHERE a.idcategoria_categorias=c.idcategoria AND p.idcategoria_categorias=c.idcategoria)AND c.idcategoria=?1 ", nativeQuery = true)
    void deleteByIdQ(Long id);

    @Query(value = "SELECT SUM(l.totaltarifa) FROM facturas f, lecturas l, rutasxemision r, emisiones e where l.idfactura = f.idfactura and r.idemision_emisiones = e.idemision and r.idrutaxemision = l.idrutaxemision_rutasxemision and e.idemision =195 and l.idcategoria = 1", nativeQuery = true)
    BigDecimal sumTotalTarifa();
}
