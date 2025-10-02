package com.erp.login.repositories;

import com.erp.login.interfaces.UsuarioI;
import com.erp.login.models.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsuariosR extends JpaRepository<Usuarios, Long> {
    // Todos (Excepto el Administrador)
    @Query(value = "SELECT * FROM usuarios order by identificausu", nativeQuery = true)
    List<Usuarios> findAll();

    // Busca un usuario por Identificación
    @Query(value = "SELECT * FROM usuarios where identificausu=?1", nativeQuery = true)
    Usuarios findByIdentificausu(String identificausu);

    @Query(value = "SELECT * FROM usuarios where identificausu=?1 AND codusu=?2", nativeQuery = true)
    Usuarios findUsuario(String a, String b);

    @Query(value = "select u.idusuario as idusuario, u.identificausu as identificacion, u.nomusu as nombre, u.alias as alias, u.estado as estado from usuarios u where idusuario = ?1", nativeQuery = true)
    UsuarioI findDatosById(Long idusuario);

}
