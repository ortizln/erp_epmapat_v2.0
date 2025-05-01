package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Convenios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConveniosR extends JpaRepository<Convenios, Long> {
    List<Convenios> findByNroconvenioBetweenOrderByNroconvenioAsc(Integer desde, Integer hasta);

    // Busca por número de convenio (para validar)
    @Query(value = "SELECT * FROM convenios AS c WHERE c.nroconvenio=?1", nativeQuery = true)
    public List<Convenios> findNroconvenio(Long nroconvenio);
    // Ultimo Número de convenio
    Convenios findFirstByOrderByNroconvenioDesc();
    // Siguiente Número de convenio
    Convenios findTopByOrderByNroconvenioDesc();
    // Valida Nroconvenio
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Convenios c WHERE c.nroconvenio = :nroconvenio")
    boolean valNroconvenio(@Param("nroconvenio") Integer nroconvenio);
    @Query(value = "select * from convenios c where idabonado = ?1 order by idconvenio desc ;", nativeQuery = true)
    public List<Convenios> findByReferencia(Long  referencia);
}
