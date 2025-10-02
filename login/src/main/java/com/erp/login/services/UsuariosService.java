package com.erp.login.services;

import com.erp.login.interfaces.UsuarioI;
import com.erp.login.models.Usuarios;
import com.erp.login.repositories.UsuariosR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuariosService {
    @Autowired
    UsuariosR dao;

    // Busca Todos (Excepto el Administrador)
    public List<Usuarios> findAll() {
        return dao.findAll();
    }

    // Busca un Usuario
    public Usuarios findUsuario(String a, String b) {
        return dao.findUsuario(a, b);
    }

    // Busca un Usuario por Identificacion
    public Usuarios findByIdentificausu(String identificausu) {
        return dao.findByIdentificausu(identificausu);
    }

    public Optional<Usuarios> findById(Long id) {
        return dao.findById(id);
    }

    public <S extends Usuarios> S save(S entity) {
        return dao.save(entity);
    }

    public UsuarioI findDatosById(Long idusuario){
        return dao.findDatosById(idusuario);
    }
}
