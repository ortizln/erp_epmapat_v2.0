package com.erp.contabilidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.contabilidad.modelo.Tabla17;

public interface Tabla17R extends JpaRepository<Tabla17, Long> {

   @Query("SELECT t FROM Tabla17 t WHERE t.porciva > 0 ORDER BY t.porciva ASC")
   List<Tabla17> findByPorcivaGreaterThanOrderByPorcivaAsc();

}

