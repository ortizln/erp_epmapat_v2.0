package com.erp.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.Catalogoitems;
import org.springframework.data.repository.query.Param;

public interface CatalogoitemsR extends JpaRepository<Catalogoitems, Long>{

   //Productos por Seccion y/o Descripcion
   @Query("""
    SELECT c
    FROM Catalogoitems c
    JOIN c.idusoitems_usoitems u
    WHERE u.idmodulo_modulos.idmodulo BETWEEN :id1 AND :id2
      AND LOWER(c.descripcion) LIKE LOWER(CONCAT('%', :descripcion, '%'))
    ORDER BY c.descripcion
    """)
   List<Catalogoitems> findProductos(
           @Param("id1") Long id1,
           @Param("id2") Long id2,
           @Param("descripcion") String descripcion
   );


   @Query(value="SELECT * FROM catalogoitems WHERE idrubro_rubros=?1 order by descripcion", nativeQuery = true)
	public List<Catalogoitems> findByIdrubro(Long idrubro);

   @Query(value="SELECT * FROM catalogoitems WHERE idusoitems_usoitems=?1 order by descripcion", nativeQuery = true)
	public List<Catalogoitems> findByIdusoitems(Long idusoitems);
   
   //Validar nombre (Por Uso)
   @Query(value = "SELECT * FROM catalogoitems where idusoitems_usoitems=?1 and LOWER(descripcion)=?2", nativeQuery=true)
	List<Catalogoitems> findByNombre(Long idusoitems, String descripcion);

}
