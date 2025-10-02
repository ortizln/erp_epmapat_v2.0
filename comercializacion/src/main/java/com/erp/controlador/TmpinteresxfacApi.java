package com.erp.controlador;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.servicio.TmpinteresxfacService;

@RestController
@RequestMapping("/tmpinteresxfac")
@CrossOrigin("*")
public class TmpinteresxfacApi {
    @Autowired
    private TmpinteresxfacService tmpinteresxfacService;

    @GetMapping("/calcularInteres")
    private ResponseEntity<Map<String, Object>> updateFacturas() {
        return ResponseEntity.ok(tmpinteresxfacService.updateTmpInteresxfac());
    }
}
