package com.erp.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.modelo.Fec_factura_detalles;
import com.erp.servicio.Fec_factura_detallesService;

@RestController
@RequestMapping("/facturadetalles")

public class Fec_factura_detallesApi {
	@Autowired
	private Fec_factura_detallesService fecfdetService;

	@GetMapping("/detalle")
	public ResponseEntity<List<Fec_factura_detalles>> getMethodName() {
		return ResponseEntity.ok(fecfdetService.findAll());
	}

	@PostMapping
	public ResponseEntity<Fec_factura_detalles> saveFacturaDetalle(@RequestBody Fec_factura_detalles fecfdetalle) {
		return ResponseEntity.ok(fecfdetService.save(fecfdetalle));
	}

	@GetMapping("/factura")
	public ResponseEntity<List<Fec_factura_detalles>> getFecDetalleByIdFactura(
			@RequestParam("idfactura") Long idfactura) {
		return ResponseEntity.ok(fecfdetService.findFecDetalleByIdFactura(idfactura));
	}

}
