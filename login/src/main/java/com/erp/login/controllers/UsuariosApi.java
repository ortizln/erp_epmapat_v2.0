package com.erp.login.controllers;

import com.erp.login.DTO.LoginRequest;
import com.erp.login.DTO.LoginResponse;
import com.erp.login.interfaces.UsuarioI;
import com.erp.login.interfaces.UsuarioLoginI;
import com.erp.login.models.Usuarios;
import com.erp.login.services.UsuariosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios")
public class UsuariosApi {
    @Autowired
    UsuariosService usuServicio;

    @GetMapping
    public List<Usuarios> getAll() { return usuServicio.findAll(); }

    @GetMapping("/usuario")
    public Usuarios getUsuario(@RequestParam(value = "a") String a, @RequestParam(value = "b") String b) {
        if (a != null && b != null) return usuServicio.findUsuario(a, b);
        return null;
    }

    @GetMapping("/identificacion")
    public Usuarios getByIdentificausu(@Param(value = "identificausu") String identificausu) {
        if (identificausu != null) return usuServicio.findByIdentificausu(identificausu);
        return null;
    }

    @GetMapping("/{idusuario}")
    public ResponseEntity<Usuarios> getByIdusuario(@PathVariable Long idusuario) {
        Usuarios usuario = usuServicio.findById(idusuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe el Usuario: " + idusuario));
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{idusuario}")
    public ResponseEntity<Usuarios> update(@PathVariable Long idusuario, @RequestBody Usuarios x) {
        Usuarios y = usuServicio.findById(idusuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe Usuario con Id: " + idusuario));

        y.setIdentificausu(x.getIdentificausu());
        y.setNomusu(x.getNomusu());
        y.setCodusu(x.getCodusu());
        y.setFdesde(x.getFdesde());
        y.setFhasta(x.getFhasta());
        y.setEstado(x.getEstado());
        y.setEmail(x.getEmail());
        y.setUsumodi(x.getUsumodi());
        y.setFecmodi(x.getFecmodi());
        y.setOtrapestania(x.getOtrapestania());
        y.setAlias(x.getAlias());
        y.setPriusu(x.getPriusu());
        y.setPerfil(x.getPerfil());
        y.setToolbarframe(x.getToolbarframe());
        y.setToolbarsheet(x.getToolbarsheet());
        y.setPlataform_access(x.getPlataform_access());

        Usuarios actualizar = usuServicio.save(y);
        return ResponseEntity.ok(actualizar);
    }

    @PostMapping
    public ResponseEntity<Object> saveUsuario(@RequestBody Usuarios user) {
        Map<String, Object> response = new HashMap<>();
        Usuarios _user = usuServicio.save(user);
        response.put("status", ResponseEntity.ok());
        response.put("message", _user != null ? "Usuario creado" : "Usuario no creado");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/one")
    public ResponseEntity<UsuarioI> findDatosById(@RequestParam Long idusuario) {
        UsuarioI usuario = usuServicio.findDatosById(idusuario);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UsuarioLoginI user = usuServicio.chargeLogin(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String passEncrypt = myFun(request.getPassword());
        boolean credencialesOk = user.getNomusu().equals(request.getUsername()) && user.getCodusu().equals(passEncrypt);
        if (!credencialesOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String platform = (request.getPlatform() == null || request.getPlatform().isBlank())
                ? "MOBILE" : request.getPlatform().trim().toUpperCase();
        String access = (user.getPlataform_access() == null || user.getPlataform_access().isBlank())
                ? "BOTH" : user.getPlataform_access().trim().toUpperCase();

        boolean allowedPlatform = "BOTH".equals(access) || platform.equals(access);
        if (!allowedPlatform) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Usuario no autorizado para plataforma " + platform);
        }

        List<String> modules = usuServicio.getEnabledModules(user.getIdusuario(), platform);
        LoginResponse response = new LoginResponse("token-jwt-falso", user.getNomusu(), user.getIdusuario(), modules);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/cargo")
    public ResponseEntity<List<UsuarioI>> getByCargo(@RequestParam(name = "ids") List<Long> idsCargo) {
        return ResponseEntity.ok(usuServicio.findByCargo(idsCargo));
    }

    public static String myFun(String x) {
        StringBuilder y = new StringBuilder();
        for (int i = 0; i < x.length(); i++) y.append((int) x.charAt(i));
        StringBuilder rtn = new StringBuilder();
        for (int i = 0; i < y.length(); i += 2) rtn.append(y.charAt(i));
        rtn.append(x.trim().length());
        for (int i = y.length() - 1; i >= 0; i -= 2) rtn.append(y.charAt(i));
        return rtn.toString();
    }
}
