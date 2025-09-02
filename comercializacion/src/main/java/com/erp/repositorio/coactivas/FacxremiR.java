package com.erp.repositorio.coactivas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.modelo.coactivas.Facxremi;

public interface FacxremiR extends JpaRepository<Facxremi, Long> {
    @Query(value = "SELECT * FROM facxremi fr where fr.idremision_remisiones = ?1 and tipfactura = ?2 order by fr.idfactura_facturas asc", nativeQuery = true)
    public List<Facxremi> findByRemision(Long idremision, Long tipfac);
}
