package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Liquidatramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LiquidaTramiteR extends JpaRepository<Liquidatramite, Long> {
    @Query(value = "SELECT * FROM liquidatrami WHERE idtramite_tramites=?1",nativeQuery = true)
    public List<Liquidatramite> findByIdTramite(Long idtramite);
}
