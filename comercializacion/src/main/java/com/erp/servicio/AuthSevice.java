package com.erp.servicio;

import com.erp.DTO.RegistroClienteRequest;
import com.erp.config.AESUtil;
import com.erp.modelo.Clientes;
import com.erp.repositorio.ClientesR;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@RequiredArgsConstructor
@Service
public class AuthSevice {
    private final ClientesR clienteRepository;


    public Clientes registrarCliente(RegistroClienteRequest req) throws Exception {
        if (clienteRepository.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("El username ya existe");
        }

        Clientes c = new Clientes();
        c.setNombre(req.getNombre());
        c.setEmail(req.getEmail());
        c.setUsername(req.getUsername());

        // 🔒 CIFRAR contraseña antes de guardar
        String passCifrada = AESUtil.cifrar(req.getPassword());
        c.setPassword(passCifrada);

        c.setActivo(true);
        c.setRol("CLIENTE");

        return clienteRepository.save(c);
    }

    public Clientes validarLogin(String username, String passwordPlano) throws Exception {
        Clientes c = clienteRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario o contraseña inválidos"));

        if (!c.isActivo()) {
            throw new IllegalArgumentException("Usuario inactivo");
        }

        // ✅ Opción 1: Cifrar lo que viene del front y comparar con DB (recomendado)
        String cifradaInput = AESUtil.cifrar(passwordPlano);
        if (!c.getPassword().equals(cifradaInput)) {
            throw new IllegalArgumentException("Usuario o contraseña inválidos");
        }

        // ✅ Opción 2: (menos recomendado) descifrar lo de BD y comparar plano:
        // String passDescifrada = AESUtil.descifrar(c.getPassword());
        // if (!passDescifrada.equals(passwordPlano)) { ... }

        return c;
    }
}
