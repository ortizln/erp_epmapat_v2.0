package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Rubroadicional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RubroadicionalR extends JpaRepository<Rubroadicional, Long> {

    @Query(value = "SELECT * FROM rubroadicional r2 INNER JOIN rubros r3 ON r2.idrubro_rubros = r3.idrubro WHERE r2.idtptramite_tptramite=?1", nativeQuery = true)
    public List<Rubroadicional> findByIdTpTramtie(Long idtptramite);
}
