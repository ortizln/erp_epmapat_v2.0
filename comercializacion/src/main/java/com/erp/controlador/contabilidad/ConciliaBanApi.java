package com.erp.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.modelo.contabilidad.ConciliaBan;
import com.erp.modelo.contabilidad.Cuentas;
import com.erp.servicio.contabilidad.ConciliaBanServicio;

@RestController
@RequestMapping("/api/conciliaban")


public class ConciliaBanApi {

	@Autowired
	private ConciliaBanServicio conciServicio; 

	@GetMapping
	public ResponseEntity<List<ConciliaBan>> getAllConciliaBan(){
		return ResponseEntity.ok(conciServicio.findAll());
	}

	@GetMapping("/")
	public ResponseEntity<ConciliaBan> getConciliaBan(@Param(value = "idcuenta") Cuentas idcuenta, @Param(value = "mes") Integer mes) {
	  ConciliaBan registro = conciServicio.getConciliaBan(idcuenta, mes);
	  if (registro != null) {
		 return ResponseEntity.ok(registro);
	  } else {
		 return ResponseEntity.notFound().build();
	  }
	}
 
	
	@PostMapping
	public ResponseEntity<ConciliaBan> saveConciliaban(@RequestBody ConciliaBan conciliaban){
		return ResponseEntity.ok(conciServicio.save(conciliaban));
	}

}
