package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Rutasxemision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RutasxemisionR extends JpaRepository<Rutasxemision, Long> {
    // Rutas por Emision
    @Query(value = "SELECT a.* FROM rutasxemision AS a JOIN rutas AS b ON a.idruta_rutas = b.idruta WHERE a.idemision_emisiones=?1 order by b.codigo", nativeQuery = true)
    public List<Rutasxemision> findByIdemision(Long idemision);

    //Cuenta las rutas abiertas de una Emisión
    @Query(value = "SELECT COUNT(*) FROM rutasxemision r WHERE r.idemision_emisiones=?1 and r.estado = 0", nativeQuery = true)
    Long contarPorEstadoYIdemision(Long idemision_emisiones);

    @Query(value = "select * from rutasxemision where idemision_emisiones = ?1 and idruta_rutas = ?2", nativeQuery = true)
    public Rutasxemision findByEmisionRuta(Long idemision, Long idruta);

}
