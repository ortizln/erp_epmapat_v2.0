package com.erp.sri_files.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.erp.sri_files.models.DocumentoElectronico;

public interface DocumentoElectronicoR extends JpaRepository<DocumentoElectronico, Long> {

    Optional<DocumentoElectronico> findByUuid(String uuid);

    Optional<DocumentoElectronico> findByExternalId(String externalId);

    Optional<DocumentoElectronico> findByClaveAcceso(String claveAcceso);

    @Query("SELECT d FROM DocumentoElectronico d WHERE d.externalId = :externalId AND d.origenSistema = :origenSistema")
    Optional<DocumentoElectronico> findByExternalIdAndOrigen(@Param("externalId") String externalId, @Param("origenSistema") String origenSistema);

    @Query("SELECT d FROM DocumentoElectronico d WHERE d.estado = :estado ORDER BY d.id ASC")
    List<DocumentoElectronico> findByEstado(@Param("estado") String estado, Pageable pageable);

    @Query("SELECT d FROM DocumentoElectronico d WHERE d.estado IN ('RECIBIDO', 'VALIDANDO') ORDER BY d.id ASC")
    List<DocumentoElectronico> findPendientesProcesamiento(Pageable pageable);

    @Query("SELECT d FROM DocumentoElectronico d WHERE d.estado IN ('PENDIENTE_AUTORIZACION', 'ENVIANDO_SRI') AND (d.intentosAutorizacion < 5 OR d.intentosAutorizacion IS NULL) ORDER BY d.id ASC")
    List<DocumentoElectronico> findPendientesAutorizacion(Pageable pageable);

    @Query("SELECT d FROM DocumentoElectronico d WHERE d.estado = 'PENDIENTE_CORREO' AND (d.intentosCorreo < 3 OR d.intentosCorreo IS NULL) ORDER BY d.id ASC")
    List<DocumentoElectronico> findPendientesCorreo(Pageable pageable);

    @Query("SELECT d FROM DocumentoElectronico d WHERE d.processingLock = true AND d.lockedAt < :cutoffTime")
    List<DocumentoElectronico> findLocksExpirados(@Param("cutoffTime") java.time.LocalDateTime cutoffTime);

    @Query("SELECT COUNT(d) FROM DocumentoElectronico d WHERE d.estado = :estado")
    long countByEstado(@Param("estado") String estado);

    boolean existsByExternalIdAndOrigenSistema(String externalId, String origenSistema);

    boolean existsByClaveAcceso(String claveAcceso);

    List<DocumentoElectronico> findByEstadoIn(List<String> estados);

    List<DocumentoElectronico> findByEstadoAndMailEnviadoFalse(String estado);

    @Query("SELECT d FROM DocumentoElectronico d WHERE d.estado = :estado AND (d.intentosEnvio < :maxIntentos OR d.intentosEnvio IS NULL) ORDER BY d.id ASC")
    List<DocumentoElectronico> findByEstadoConIntentosLimit(@Param("estado") String estado, @Param("maxIntentos") int maxIntentos);
}
