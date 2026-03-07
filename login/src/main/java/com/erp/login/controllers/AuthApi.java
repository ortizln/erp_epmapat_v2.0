package com.erp.login.controllers;

import com.erp.login.DTO.LoginRequest;
import com.erp.login.DTO.LoginResponse;
import com.erp.login.interfaces.UsuarioI;
import com.erp.login.interfaces.UsuarioLoginI;
import com.erp.login.services.UsuariosService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthApi {
    private final UsuariosService usuServicio;

    public AuthApi(UsuariosService usuServicio) {
        this.usuServicio = usuServicio;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UsuarioLoginI user = usuServicio.chargeLogin(request.getUsername());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String passEncrypt = UsuariosApi.myFun(request.getPassword());
        boolean credencialesOk = user.getNomusu().equals(request.getUsername()) && user.getCodusu().equals(passEncrypt);
        if (!credencialesOk) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String platform = (request.getPlatform() == null || request.getPlatform().isBlank())
                ? "WEB" : request.getPlatform().trim().toUpperCase();
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

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody(required = false) Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("token", "token-jwt-falso"));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestParam Long idusuario) {
        UsuarioI user = usuServicio.findDatosById(idusuario);
        if (user == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        return ResponseEntity.ok(user);
    }
}
