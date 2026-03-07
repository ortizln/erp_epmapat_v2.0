package com.erp.login.repositories;

import com.erp.login.models.Erpmodulos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ErpmodulosR extends JpaRepository<Erpmodulos, Long> {
    @Query("""
            SELECT e FROM Erpmodulos e
            WHERE UPPER(COALESCE(e.platform,'BOTH')) IN (UPPER(:platform), 'BOTH')
            ORDER BY e.iderpmodulo
            """)
    List<Erpmodulos> findByPlatform(@Param("platform") String platform);
}
