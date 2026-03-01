package com.erp.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.DTO.RecargoXCtaReq;
import com.erp.DTO.ValidarRecargosRequest;
import com.erp.DTO.ValidarRecargosResponse;
import com.erp.modelo.Recargosxcuenta;
import com.erp.servicio.RecargosxcuentaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recargosxcuenta")
public class RecargosxcuentaApi {
    private final RecargosxcuentaService recargosxcuentaService;

    @GetMapping("/byEmision")
    public List<Recargosxcuenta> getRecargosxcuentaByEmision(@RequestParam Long idemision) {
        return recargosxcuentaService.findAllByEmision(idemision);
    }

    @PostMapping("/validar")
    public ResponseEntity<ValidarRecargosResponse> validar(@RequestBody ValidarRecargosRequest req) {
        return ResponseEntity.ok(recargosxcuentaService.validar(req));
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Recargosxcuenta>> guardarBatch(@RequestBody List<RecargoXCtaReq> reqs) {
        return ResponseEntity.ok(recargosxcuentaService.guardarBatchConValidaciones(reqs));
    }
}
