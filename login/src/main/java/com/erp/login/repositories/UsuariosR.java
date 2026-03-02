package com.erp.login.repositories;

import com.erp.login.interfaces.UsuarioI;
import com.erp.login.interfaces.UsuarioLoginI;
import com.erp.login.models.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuariosR extends JpaRepository<Usuarios, Long> {
    @Query(value = "SELECT * FROM usuarios order by identificausu", nativeQuery = true)
    List<Usuarios> findAll();

    @Query(value = "SELECT * FROM usuarios where identificausu=?1", nativeQuery = true)
    Usuarios findByIdentificausu(String identificausu);

    @Query(value = "SELECT * FROM usuarios where identificausu=?1 AND codusu=?2", nativeQuery = true)
    Usuarios findUsuario(String a, String b);

    @Query(value = "select u.idusuario as idusuario, u.identificausu as identificacion, u.nomusu as nombre, u.alias as alias, u.estado as estado from usuarios u where idusuario = ?1", nativeQuery = true)
    UsuarioI findDatosById(Long idusuario);

    @Query(value = "select u.nomusu as nomusu, u.codusu as codusu , u.idusuario as idusuario , u.plataform_access as plataform_access from usuarios u where u.nomusu = ?1 ", nativeQuery = true)
    UsuarioLoginI chargeLogin(String nomusu);

    @Query(value = """
            SELECT u.idusuario as idusuario, u.nomusu as nombre, u.estado as estado, c.descripcion as alias
            FROM usuarios u
            JOIN personal p ON u.personal_idpersonal = p.idpersonal
            JOIN cargos c ON p.idcargo_cargos = c.idcargo
            WHERE p.idcargo_cargos IN (:idsCargo)
            """, nativeQuery = true)
    List<UsuarioI> findByCargoIn(@Param("idsCargo") List<Long> idsCargo);
}
