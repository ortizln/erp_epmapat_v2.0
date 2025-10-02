package com.erp.login.repositories;

import com.erp.login.models.Acceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccesoR extends JpaRepository<Acceso, Long> {

    @Query(value = "SELECT * FROM acceso order by codacc", nativeQuery = true)
    public List<Acceso> findAll();
}
