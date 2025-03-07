package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Aguatramite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AguatramiteR extends JpaRepository<Aguatramite, Long> {
    @Query(value = "SELECT * FROM aguatramite order by idaguatramite DESC LIMIT 10", nativeQuery = true)
    public List<Aguatramite> findAll();

    @Query(value = "SELECT * FROM aguatramite AS a WHERE a.idaguatramite >= ?1 and a.idaguatramite <= ?2 ", nativeQuery = true)
    public List<Aguatramite> findAll(Long desde, Long hasta);

    @Query(value = "SELECT * FROM aguatramite AS a JOIN clientes as c ON a.idcliente_clientes=c.idcliente WHERE c.nombre like %?1% OR LOWER(c.nombre) like %?1% or UPPER(c.nombre) like %?1% OR INITCAP(c.nombre) like %?1% ORDER by c.nombre", nativeQuery = true)
    public List<Aguatramite> findByCliente(String nombre);

    @Query(value = "SELECT * FROM aguatramite WHERE idtipotramite_tipotramite = ?1 and estado = ?2 AND ((feccrea BETWEEN ?3 AND ?4)OR( fechaterminacion BETWEEN ?5 AND ?6) )ORDER BY feccrea DESC", nativeQuery = true)
    public List<Aguatramite> findByIdTipTramite(Long idtipotramite, Long estado, LocalDate d, LocalDate h, LocalDate td, LocalDate th);

}
