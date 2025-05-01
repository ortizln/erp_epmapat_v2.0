package com.erp.comercializacion.services;

import com.erp.comercializacion.interfaces.UsuarioI;
import com.erp.comercializacion.models.Usuarios;
import com.erp.comercializacion.repositories.UsuariosR;
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
