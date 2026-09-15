package com.erp.sri_files.controllers;

import com.erp.sri_files.services.FacturaRevisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/facturas")
public class FacturaRevisionController {
    private final FacturaRevisionService revision;

    @GetMapping("/diagnostico")
    public Object diagnostico(@RequestParam(required = false) LocalDate desde,
            @RequestParam(required = false) LocalDate hasta, @RequestParam(defaultValue = "E") String estado,
            @RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "50") int limite) {
        LocalDate inicio = desde == null ? LocalDate.now().withDayOfMonth(1) : desde;
        return revision.diagnosticar(inicio, hasta == null ? inicio.withDayOfMonth(1).plusMonths(1) : hasta, estado, pagina, limite);
    }
    @GetMapping("/{id}/validacion")
    public Object validar(@PathVariable Long id) { return revision.diagnosticar(id); }

    @PostMapping("/{id}/recuperar")
    public Object recuperar(@PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean claveOriginalVerificada) {
        return revision.recuperar(id, claveOriginalVerificada);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalido(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}
