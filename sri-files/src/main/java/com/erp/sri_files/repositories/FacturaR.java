package com.erp.sri_files.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.sri_files.models.Factura;

public interface FacturaR extends JpaRepository<Factura, Long> {
    boolean existsByClaveaccesoAndIdfacturaNot(String claveacceso, Long idfactura);

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM Factura f WHERE f.idfactura = :id")
    Optional<Factura> findParaProcesar(@Param("id") Long id);

    @Query("SELECT f FROM Factura f WHERE f.fechaemision >= :desde AND f.fechaemision < :hasta "
            + "AND UPPER(TRIM(COALESCE(f.estado, ''))) = UPPER(TRIM(:estado)) ORDER BY f.idfactura")
    Page<Factura> findParaDiagnostico(@Param("desde") java.time.LocalDateTime desde,
            @Param("hasta") java.time.LocalDateTime hasta, @Param("estado") String estado, Pageable pageable);
    
    Factura findByIdfactura(Long idfactura);
    
    Optional<Factura> findByClaveacceso(String claveacceso);
    
    @Query("SELECT f FROM Factura f WHERE UPPER(TRIM(COALESCE(f.estado, ''))) = UPPER(TRIM(:estado)) ORDER BY f.idfactura ASC")
    List<Factura> _findByEstado(@Param("estado") String estado, Pageable pageable);
    
    @Query("SELECT f FROM Factura f WHERE UPPER(TRIM(COALESCE(f.estado, ''))) = UPPER(TRIM(:estado)) ORDER BY f.idfactura ASC")
    Page<Factura> findByEstadoNormalizado(@Param("estado") String estado, Pageable pageable);
    
    @Query("SELECT f FROM Factura f WHERE f.estado IN ('I', 'P') ORDER BY f.idfactura ASC")
    List<Factura> findPendientesEnvio(Pageable pageable);
    
    @Query("SELECT f FROM Factura f WHERE f.estado IN ('C', 'O') AND (f.intentos_autorizacion < 5 OR f.intentos_autorizacion IS NULL) ORDER BY f.idfactura ASC")
    List<Factura> findPendientesAutorizacion(Pageable pageable);

    @Query("SELECT f FROM Factura f WHERE UPPER(TRIM(COALESCE(f.estado, ''))) = 'A' "
            + "AND (f.xmlautorizado IS NULL OR TRIM(f.xmlautorizado) = '') ORDER BY f.idfactura ASC")
    Page<Factura> findAutorizadasSinXml(Pageable pageable);
    
    @Query("SELECT f FROM Factura f WHERE f.fecha_autorizacion IS NULL AND f.fechaemision IS NOT NULL")
    List<Factura> findSinFechaAutorizacion();

    @Query("SELECT f FROM Factura f WHERE UPPER(TRIM(COALESCE(f.estado, ''))) = 'M' ORDER BY f.idfactura ASC")
    List<Factura> findDevueltas(Pageable pageable);
}
