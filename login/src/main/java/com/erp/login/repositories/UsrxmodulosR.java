package com.erp.login.repositories;

import com.erp.login.interfaces.ErpModulosI;
import com.erp.login.models.Usrxmodulos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsrxmodulosR extends JpaRepository<Usrxmodulos, Long> {
    @Query(value = "SELECT em.descripcion, um.enabled FROM usrxmodulos um join erpmodulos em on um.iderpmodulo_erpmodulos = em.iderpmodulo where um.idusuario_usuarios = ?1 order by um.iderpmodulo_erpmodulos asc", nativeQuery = true)
    List<ErpModulosI> findModulosEnabledByUser(Long iduser);

    @Query(value = "SELECT * from usrxmodulos um where um.idusuario_usuarios = ?1 order by um.iderpmodulo_erpmodulos asc", nativeQuery = true)
    List<Usrxmodulos> FindByUser(Long iduser);

    @Query(value = "SELECT * FROM usrxmodulos um where um.idusuario_usuarios = ?1 and um.iderpmodulo_erpmodulos = ?2", nativeQuery = true)
    Usrxmodulos findModulos(Long iduser, Long iderpmodulo);

    @Query(value = """
            SELECT m.descripcion
            FROM usrxmodulos um
            JOIN erpmodulos m ON m.iderpmodulo = um.iderpmodulo_erpmodulos
            WHERE um.idusuario_usuarios = :userId
              AND um.enabled = true
              AND (um.platform = :platform OR um.platform = 'BOTH')
            ORDER BY um.iderpmodulo_erpmodulos
            """, nativeQuery = true)
    List<String> findEnabledModuleNamesByUserAndPlatform(@Param("userId") Long userId,
                                                          @Param("platform") String platform);
}
