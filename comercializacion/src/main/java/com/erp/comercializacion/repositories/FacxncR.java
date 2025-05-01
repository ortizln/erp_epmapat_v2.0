package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Facxnc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FacxncR extends JpaRepository<Facxnc, Long> {
    @Query(value = "SELECT * FROM facxnc fnc WHERE fnc.idvaloresnc_valoresnc ",nativeQuery = true)
    List<Facxnc> findByIdvalnc(Long idvalnc);
}
