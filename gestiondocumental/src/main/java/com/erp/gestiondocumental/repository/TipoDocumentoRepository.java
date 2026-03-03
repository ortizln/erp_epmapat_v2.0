package com.erp.gestiondocumental.repository;

import com.erp.gestiondocumental.model.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TipoDocumentoRepository extends JpaRepository<TipoDocumento, UUID> {
    List<TipoDocumento> findByEntidadIdOrderByCodigo(UUID entidadId);
}
