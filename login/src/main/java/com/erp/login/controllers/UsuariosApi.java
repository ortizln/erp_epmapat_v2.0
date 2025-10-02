package com.erp.login.controllers;

import com.erp.login.interfaces.UsuarioI;
import com.erp.login.models.Usuarios;
import com.erp.login.services.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuariosApi {
    @Autowired
    UsuariosService usuServicio;

    @GetMapping
    public List<Usuarios> getAll() {
        return usuServicio.findAll();
    }

    @GetMapping("/usuario")
    public Usuarios getUsuario(@RequestParam(value = "a") String a, @RequestParam(value = "b") String b) {
        if (a != null && b != null) {
            return usuServicio.findUsuario(a, b);
        } else {
            return null;
        }
    }

    // Va a servir para validar
    @GetMapping("/identificacion")
    public Usuarios getByIdentificausu(@Param(value = "identificausu") String identificausu) {
        if (identificausu != null) {
            return usuServicio.findByIdentificausu(identificausu);
        } else {
            return null;
        }
    }

    @GetMapping("/{idusuario}")
    public ResponseEntity<Usuarios> getByIdusuario(@PathVariable Long idusuario) {
        Usuarios usuario = usuServicio.findById(idusuario)
                .orElseThrow(null);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{idusuario}")
    public ResponseEntity<Usuarios> update(@PathVariable Long idusuario, @RequestBody Usuarios x) {
        Usuarios y = usuServicio.findById(idusuario)
                .orElseThrow(null);
        y.setIdentificausu(x.getIdentificausu());
        y.setNomusu(x.getNomusu());
        y.setCodusu(x.getCodusu());
        y.setFdesde(x.getFdesde());
        y.setFhasta(x.getFhasta());
        y.setEstado(x.getEstado());
        y.setEmail(x.getEmail());
        y.setFeccrea(x.getFeccrea());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());
        y.setOtrapestania(x.getOtrapestania());
        y.setAlias(x.getAlias());
        y.setPriusu(x.getPriusu());
        y.setPerfil(x.getPerfil());
        y.setToolbarframe(x.getToolbarframe());
        y.setToolbarsheet(x.getToolbarsheet());

        Usuarios actualizar = usuServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @PostMapping
    public ResponseEntity<Object> saveUsuario(@RequestBody Usuarios user) {
        Map<String, Object> response = new HashMap<>();
        Usuarios _user= usuServicio.save(user);
        if(_user != null ){
            response.put("status", ResponseEntity.ok());
            response.put("message", "Usuario creado");

        }else{
            response.put("status", ResponseEntity.ok());
            response.put("message", "Usuario no creado");
        }
        return ResponseEntity.ok(response);

    }

    @GetMapping("/one")
    public ResponseEntity<UsuarioI> findDatosById(@RequestParam Long idusuario) {
        UsuarioI usuario = usuServicio.findDatosById(idusuario);
        return ResponseEntity.ok(usuario);
    }
}
