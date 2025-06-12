package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.models.Fec_factura_pagos;
import com.erp.comercializacion.services.Fec_factura_pagosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturapagos")
@CrossOrigin(origins = "*")
public class Fec_factura_pagosApi {
    @Autowired
    private Fec_factura_pagosService fecfpagosService;

    @PostMapping
    public ResponseEntity<Fec_factura_pagos> postMethodName(@RequestBody Fec_factura_pagos fecfpagos) {

        return ResponseEntity.ok(fecfpagosService.save(fecfpagos));
    }

    @GetMapping("/pagos")
    public ResponseEntity<List<Fec_factura_pagos>> findAll() {
        return ResponseEntity.ok(fecfpagosService.findAll());
    }

    @GetMapping("/factura")
    public ResponseEntity<List<Fec_factura_pagos>> getByIdfactura(@RequestParam("idfactura") Long idfactura) {
        return ResponseEntity.ok(fecfpagosService.findByIdFactura(idfactura));
    }
}
