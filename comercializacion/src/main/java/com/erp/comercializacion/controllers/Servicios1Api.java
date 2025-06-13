package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Servicios1;
import com.erp.comercializacion.services.Servicios1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servicios1")
@CrossOrigin("*")

public class Servicios1Api {

	@Autowired
	private Servicios1Service servicios1S;
	
	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Servicios1> findAllModulos(){
		return servicios1S.findAll();
	}

	@PostMapping
	public Servicios1 saveServicios(@RequestBody Servicios1 servicios1M) {
		return servicios1S.save(servicios1M);
	}

   @GetMapping("/modulo/{idmodulo}")
   public List<Servicios1> findByIdModulos(@PathVariable Long idmodulo){
    	return servicios1S.findByIdModulos(idmodulo);
   }

   @GetMapping("/{idservicio}")
   public ResponseEntity<Servicios1> getByIdServicios(@PathVariable("idservicio") Long idservicio){
	   Servicios1 servicios1M = servicios1S.findById(idservicio)
   		.orElseThrow(()->new ResourceNotFoundExcepciones("No existe este servicio con este Id"+idservicio));
    	return ResponseEntity.ok(servicios1M);
   }

}
