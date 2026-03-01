package com.erp.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.erp.interfaces.mobile.EstadomMobile;
import com.erp.modelo.Estadom;

public interface EstadomR extends JpaRepository<Estadom, Long> {

    @Query(value = "SELECT e.idestadom AS idestadom, e.descripcion AS descripcion FROM estadom e ORDER BY e.idestadom", nativeQuery = true)
    List<EstadomMobile> findAllEstadom();
}
