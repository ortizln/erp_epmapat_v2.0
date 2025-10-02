package com.erp.login.repositories;

import com.erp.login.models.Colores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ColoresR extends JpaRepository<Colores, Long> {
    @Query(value = "SELECT * FROM colores where LENGTH(codigo) = 2 order by idcolor", nativeQuery=true)
    public List<Colores> findTonos();

    @Query(value = "SELECT * FROM colores where codigo like ?1% and LENGTH(codigo) = 4 order by idcolor", nativeQuery=true)
    public List<Colores> findByTono(String codigo);
}
