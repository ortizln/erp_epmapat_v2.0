package com.erp.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.interfaces.UsrxrutasService;
import com.erp.modelo.Usrxrutas;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usrxrutas")
@RequiredArgsConstructor
public class UsrxrutasApi {

    private final UsrxrutasService usrxrutasService;

    @PostMapping
    public ResponseEntity<Usrxrutas> crear(@RequestBody Usrxrutas body) {
        return ResponseEntity.ok(usrxrutasService.crear(body));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usrxrutas> actualizar(@PathVariable Long id, @RequestBody Usrxrutas body) {
        return ResponseEntity.ok(usrxrutasService.actualizar(id, body));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usrxrutas> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(usrxrutasService.obtenerPorId(id));
    }

    @GetMapping
    public ResponseEntity<List<Usrxrutas>> listar() {
        return ResponseEntity.ok(usrxrutasService.listar());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usrxrutasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/usuario/{idusuario}")
    public ResponseEntity<List<Usrxrutas>> listarPorUsuario(@PathVariable Long idusuario) {
        return ResponseEntity.ok(usrxrutasService.listarPorUsuario(idusuario));
    }

    @GetMapping("/emision/{idemision}")
    public ResponseEntity<List<Usrxrutas>> listarPorEmision(@PathVariable Long idemision) {
        return ResponseEntity.ok(usrxrutasService.listarPorEmision(idemision));
    }

    @GetMapping("/usuario/{idusuario}/emision/{idemision}")
    public ResponseEntity<Usrxrutas> obtenerPorUsuarioYEmision(@PathVariable Long idusuario,
            @PathVariable Long idemision) {

        Usrxrutas resultado = usrxrutasService
                .findByUsuarioAndEmision(idusuario, idemision)
                .orElseGet(Usrxrutas::new);

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/emision/{idemision}/rutas-ocupadas")
    public ResponseEntity<List<Long>> rutasOcupadas(@PathVariable Long idemision) {
        return ResponseEntity.ok(usrxrutasService.rutasOcupadasEnEmision(idemision));
    }
}
