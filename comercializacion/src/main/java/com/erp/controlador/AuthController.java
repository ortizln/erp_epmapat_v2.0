package com.erp.controlador;

import com.erp.DTO.LoginRequest;
import com.erp.DTO.RegistroClienteRequest;
import com.erp.modelo.Clientes;
import com.erp.servicio.AuthSevice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthSevice authService;
    // podrías inyectar aquí una clase JwtUtil más adelante

    @PostMapping("/registro-cliente")
    public ResponseEntity<?> registrar(@RequestBody RegistroClienteRequest req) {
        try {
            Clientes c = authService.registrarCliente(req);
            return ResponseEntity.ok(Map.of(
                    "idcliente", c.getIdcliente(),
                    "username", c.getUsername(),
                    "email", c.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Clientes c = authService.validarLogin(req.getUsername(), req.getPassword());

            String fakeToken = "fake-token-" + c.getIdcliente();

            Map<String, Object> user = Map.of(
                    "idcliente", c.getIdcliente(),
                    "username", c.getUsername(),
                    "email", c.getEmail() != null ? c.getEmail() : ""
            );

            Map<String, Object> resp = Map.of(
                    "token", fakeToken,
                    "user", user
            );

            return ResponseEntity.ok(resp);

        } catch (RuntimeException e) {
            // Usuario/clave incorrectos, etc.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
