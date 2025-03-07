package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Ccertificaciones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CcertificacionesR extends JpaRepository<Ccertificaciones, Long> {
    //Desde / Hasta número
    @Query(value = "SELECT * FROM ccertificaciones AS c WHERE c.numero >= ?1 and c.numero <= ?2 ORDER BY numero", nativeQuery = true)
    public List<Ccertificaciones> findDesdeHasta(Long desde, Long hasta);

    //Por Cliente
    @Query(value = "SELECT * FROM ccertificaciones AS c JOIN facturas as f ON c.idfactura_facturas=f.idfactura JOIN clientes as cl ON f.idcliente=cl.idcliente WHERE (LOWER(cl.nombre) like %?1%) ORDER by cl.nombre", nativeQuery = true)
    public List<Ccertificaciones> findByCliente(String cliente);

    // Ultima Certificación
    Ccertificaciones findFirstByOrderByIdccertificacionDesc();
}
