package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Modulos;
import com.erp.comercializacion.services.ModulosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/modulos")
@CrossOrigin(origins = "*")
public class ModulosApi {

	@Autowired
	private ModulosService moduServicio;

	@GetMapping
	public List<Modulos> findAll(){
		return moduServicio.findAll();
	}

	@GetMapping("/s_modulos")
	public List<Modulos> getTwoModulos(){
		return moduServicio.getTwoModulos();
	}
	
	@GetMapping("/{idmodulo}")
	public ResponseEntity<Modulos> getByIdRubros(@PathVariable("idmodulo") Long idmodulo){
		Modulos modulos = moduServicio.findById(idmodulo)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No existe el modulo con Id: " + idmodulo));
		return ResponseEntity.ok(modulos);
	}

}
