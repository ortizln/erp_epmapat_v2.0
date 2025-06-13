package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Rubros;
import com.erp.comercializacion.models.Tramites1;
import com.erp.comercializacion.services.RubrosService;
import com.erp.comercializacion.services.Tramites1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tramites1")
@CrossOrigin("*")
public class Tramites1Api {

	@Autowired
	private Tramites1Service tramites1S;
	@Autowired
	private RubrosService rubrosS;

	@GetMapping
	public List<Tramites1> getAllTramites(){
		return tramites1S.findAll();
	}

	@PostMapping
	public Tramites1 saveTramites(@RequestBody Tramites1 tramites1M) {
		return tramites1S.save(tramites1M);
	}

	@PutMapping("/{idtramite}/{idrubro}")
	public Tramites1 addRubrosxTramie(@PathVariable Long idtramite, @PathVariable Long idrubro) {
		Tramites1 tramite1M = tramites1S.findById(idtramite).get();
		Rubros rubrosM = rubrosS.findById(idrubro).get();
		tramite1M.addRubros(rubrosM);
		return tramites1S.save(tramite1M);	
	}

	@GetMapping("/{idtramite}")
	public ResponseEntity<Tramites1> getTramiteById(@PathVariable ("idtramite") Long idtramite){
		Tramites1 tramite1M = tramites1S.findById(idtramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No existe este tramie con este Id: "+idtramite));
		return ResponseEntity.ok(tramite1M);
	}

}
