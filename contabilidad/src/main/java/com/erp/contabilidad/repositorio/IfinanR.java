package com.erp.contabilidad.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.contabilidad.modelo.Ifinan;

public interface IfinanR extends JpaRepository<Ifinan, Long> {
   @Query(value = "SELECT * FROM ifinan WHERE codifinan LIKE ?1% order by codifinan", nativeQuery = true)
   List<Ifinan> findByCodifinan(String codifinan);

   @Query(value = "SELECT * FROM ifinan WHERE LOWER(nomifinan) LIKE %?1% order by nomifinan", nativeQuery = true)
   List<Ifinan> findByNomifinan(String nomifinan);
}
