package com.erp.login.repositories;

import com.erp.login.interfaces.UsuarioI;
import com.erp.login.interfaces.UsuarioLoginI;
import com.erp.login.interfaces.UsuarioPersonalI;
import com.erp.login.models.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuariosR extends JpaRepository<Usuarios, Long> {
    @Query(value = "SELECT * FROM usuarios order by identificausu", nativeQuery = true)
    List<Usuarios> findAll();

    @Query(value = """
            SELECT u.idusuario as idusuario,
                   u.identificausu as identificausu,
                   u.alias as alias,
                   u.nomusu as nomusu,
                   u.estado as estado,
                   u.personal_idpersonal as personalIdpersonal,
                   CASE WHEN p.idpersonal IS NULL THEN NULL ELSE (COALESCE(p.apellidos,'') || ' ' || COALESCE(p.nombres,'')) END as personalNombre
            FROM usuarios u
            LEFT JOIN personal p ON p.idpersonal = u.personal_idpersonal
            ORDER BY u.identificausu
            """, nativeQuery = true)
    List<UsuarioPersonalI> findAllWithPersonal();

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
