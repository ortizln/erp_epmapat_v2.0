package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Suspensiones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SuspensionesR extends JpaRepository<Suspensiones, Long> {
    @Query(value="SELECT * FROM suspensiones WHERE tipo = 1 AND fecha BETWEEN (?1) AND (?2)", nativeQuery = true)
    public List<Suspensiones> findByFecha(LocalDate desde, LocalDate hasta);
    @Query(value="SELECT * FROM suspensiones WHERE tipo = 2 AND fecha BETWEEN (?1) AND (?2)", nativeQuery = true)
    public List<Suspensiones> findByFechaHabilitaciones(LocalDate desde, LocalDate hasta);
    @Query(value = "SELECT * FROM suspensiones s WHERE tipo = 1 ORDER BY idsuspension DESC LIMIT 10", nativeQuery = true)
    public List<Suspensiones> findLastTen();
    @Query(value = "SELECT * FROM suspensiones s WHERE numero = ?1", nativeQuery = true)
    public List<Suspensiones> findByNumero(Long numero);
    @Query(value = "SELECT * FROM suspensiones s WHERE tipo = 2 ORDER BY idsuspension DESC LIMIT 10", nativeQuery = true)
    public List<Suspensiones> findHabilitaciones();

    Suspensiones findFirstByOrderByIdsuspensionDesc();
}
