package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Abonadosxsuspension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AbonadosxsuspensionR extends JpaRepository<Abonadosxsuspension, Long> {

    @Query(value = "SELECT * FROM aboxsuspension WHERE idsuspension_suspensiones=?1", nativeQuery = true)
    public List<Abonadosxsuspension> findByIdsuspension(Long idsuspension);

}
