package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Tipotramite;
import com.erp.comercializacion.services.TipotramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/tipotramite")
@CrossOrigin("*")
public class TipoTramiteApi {

	@Autowired
	private TipotramiteService tipotramiteS;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Tipotramite> getAllTipoTramite(){
		return tipotramiteS.findAll(Sort.by(Sort.Order.asc("descripcion")));
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Tipotramite saveTipoTramite(@RequestBody Tipotramite tipotramiteM) {
		return tipotramiteS.save(tipotramiteM);
	}

	@GetMapping("/{idtipotramite}")
	public ResponseEntity<Tipotramite> getByIdTipoTramite(@PathVariable Long idtipotramite){
		Tipotramite tipotramiteM = tipotramiteS.findById(idtipotramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No se encuentra este tipo de tramite: "+ idtipotramite));
		return ResponseEntity.ok(tipotramiteM);
	}

	@PutMapping("/{idtipotramite}")
	public ResponseEntity<Tipotramite> updateTipoTramite(@PathVariable Long idtipotramite, @RequestBody Tipotramite tipotramitem){
		Tipotramite tipotramiteM = tipotramiteS.findById(idtipotramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No se encuentra este tipo de tramite: "+ idtipotramite));
		tipotramiteM.setDescripcion(tipotramitem.getDescripcion());
		tipotramiteM.setFacturable(tipotramitem.getFacturable());
		tipotramiteM.setUsucrea(tipotramitem.getUsucrea());
		tipotramiteM.setFeccrea(tipotramitem.getFeccrea());
		tipotramiteM.setUsumodi(tipotramitem.getUsumodi());
		tipotramiteM.setFecmodi(tipotramitem.getFecmodi());
		Tipotramite updateTipoTramite = tipotramiteS.save(tipotramiteM);
		return ResponseEntity.ok(updateTipoTramite);
	}
	
	@DeleteMapping(value = "/{idtipotramite}")
	public ResponseEntity<Object> deleteTipoTramite(@PathVariable("idtipotramite") Long idtipotramite){
		tipotramiteS.deleteById(idtipotramite);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	
}
