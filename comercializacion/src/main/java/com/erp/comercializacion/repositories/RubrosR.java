package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Rubros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RubrosR extends JpaRepository<Rubros, Long> {

    // Rubros de un Módulo
    @Query(value = "SELECT * FROM rubros WHERE idmodulo_modulos=?1 order by descripcion", nativeQuery = true)
    public List<Rubros> findByIdmodulo(Long idmodulo);

    // Por Módulo y Descripción
    @Query(value = "SELECT * FROM rubros WHERE idmodulo_modulos=?1 and LOWER(descripcion) like %?2% and estado=true order by descripcion", nativeQuery = true)
    public List<Rubros> findByModulo(Long idmodulo, String descripcion);

    // Validar por Nombre y Sección
    @Query(value = "SELECT * FROM rubros WHERE idmodulo_modulos=?1 and LOWER(descripcion)=?2", nativeQuery = true)
    public List<Rubros> findByNombre(Long idmodulo, String descripcion);

    // Rubros de Emisión
    @Query(value = "SELECT * FROM rubros WHERE idmodulo_modulos=4 and estado order by idrubro", nativeQuery = true)
    public List<Rubros> findEmision();

    @Query(value = "SELECT * FROM rubros WHERE idrubro = ?1", nativeQuery = true)
    public Rubros findByIdRubro(Long idrubro);

    // Validar por Nombre y Sección
    @Query(value = "SELECT * FROM rubros WHERE LOWER(descripcion)=?1 ORDER BY idrubro desc", nativeQuery = true)
    public List<Rubros> findByName(String descripcion);
}
