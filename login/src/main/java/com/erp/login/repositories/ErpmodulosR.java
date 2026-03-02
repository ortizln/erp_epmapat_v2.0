package com.erp.login.repositories;

import com.erp.login.models.Erpmodulos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ErpmodulosR extends JpaRepository<Erpmodulos, Long> {
    List<Erpmodulos> findByPlatform(String platform);
}
