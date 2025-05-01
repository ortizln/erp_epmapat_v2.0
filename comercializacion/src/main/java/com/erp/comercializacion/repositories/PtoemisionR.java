package com.erp.comercializacion.repositories;

import com.erp.comercializacion.models.Cajas;
import com.erp.comercializacion.models.Ptoemision;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PtoemisionR extends JpaRepository<Ptoemision, Long> {
    @Query(value = "SELECT * FROM cajas AS c JOIN ptoemision AS p ON c.idptoemision_ptoemision = p.idptoemision ORDER BY p.establecimiento, c.codigo ASC", nativeQuery = true)
    public List<Cajas> find_All();
    //Validación de Códigos (Establecimiento + Pto de emision)
    @Query(value = "SELECT * FROM cajas AS c JOIN ptoemision AS p ON c.idptoemision_ptoemision = p.idptoemision WHERE c.idptoemision_ptoemision=?1 AND c.codigo=?2", nativeQuery = true)
    public List<Cajas> findByCodigos(Long idptoemision, String codigo);
    //Validación de Descripcion
    @Query(value = "SELECT * FROM cajas AS c WHERE c.descripcion=?1", nativeQuery = true)
    List<Cajas> findByDescri(String descripcion);
    //Puntos de emision por Establecimiento
    @Query(value = "SELECT * FROM cajas WHERE idptoemision_ptoemision=?1 ORDER BY codigo", nativeQuery=true)
    public List<Cajas> findByIdptoemision(Long idptoemision);
    @Query(value = "SELECT * FROM cajas WHERE idusuario_usuarios = ?1", nativeQuery = true)
    public Cajas findCajaByIdUsuario(Long idusuario);
    @Query(value = "SELECT * FROM cajas WHERE not idusuario_usuarios is null and estado = 1 ", nativeQuery = true)
    public List<Cajas> findCajasActivas();

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM ptoemision AS p WHERE NOT EXISTS(SELECT * FROM cajas AS c WHERE c.idptoemision_ptoemision=p.idptoemision)AND p.idptoemision=?1 ", nativeQuery = true)
    void deleteByIdQ(Long id);

    @Query(value = "SELECT * FROM ptoemision AS p WHERE EXISTS(SELECT * FROM cajas AS c WHERE c.idptoemision_ptoemision=p.idptoemision)AND p.idptoemision=?1 ", nativeQuery = true)
    List<Ptoemision> used(Long id);


}
