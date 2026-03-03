package com.erp.gestiondocumental.repositories;

import com.erp.gestiondocumental.models.Series;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeriesR extends JpaRepository<Series, Long> {
}

