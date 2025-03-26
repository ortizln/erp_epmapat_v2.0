package com.erp.pagosonline.controllers;

import com.erp.pagosonline.DTO.FacturaDTO;
import com.erp.pagosonline.interfaces.FacturasSinCobroInter;
import com.erp.pagosonline.services.FacturasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@CrossOrigin("*")
public class FacturasApi {
    @Autowired
    private FacturasService facturasService;
    @GetMapping("/sincobrar")
    public ResponseEntity<FacturaDTO> getFacturasSinCobro(Long cuenta){
        return ResponseEntity.ok(facturasService.findFacturasSinCobro(cuenta));
    }
}
