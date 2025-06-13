package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Ptoemision;
import com.erp.comercializacion.services.PtoemisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ptoemision")
@CrossOrigin(origins = "*")

public class PtoEmisionApi {

	@Autowired
	private PtoemisionService ptoServicio;

	@GetMapping
	public List<Ptoemision> getAllPtoEmision(@Param(value = "idused") Long idused){
		if(idused == null) {
			return ptoServicio.findAll(Sort.by(Sort.Order.asc("establecimiento")));
		}else {
			return ptoServicio.used(idused);
		}
	}

	@PostMapping
	public Ptoemision savePtoEmision(@RequestBody Ptoemision ptoemisionM) {
		return ptoServicio.save(ptoemisionM);
	}

	@GetMapping("/{idptoemision}")
	public ResponseEntity<Ptoemision> getByIdPtoEmision(@PathVariable Long idptoemision){
		Ptoemision ptoemisionM = ptoServicio.findById(idptoemision)
				.orElseThrow(()-> new ResourceNotFoundExcepciones(("No extiste esta Pto Emision con el ID: "+idptoemision)));
		return ResponseEntity.ok(ptoemisionM);
	}

	@PutMapping("/{idptoemision}")
	public ResponseEntity<Ptoemision> updatePtoEmision(@PathVariable Long idptoemision, @RequestBody Ptoemision ptoemisionm){
		Ptoemision ptoemisionM = ptoServicio.findById(idptoemision)
				.orElseThrow(()->new ResourceNotFoundExcepciones(("No existe esta Pto Emision con el Id: "+ idptoemision)));
		ptoemisionM.setEstablecimiento(ptoemisionm.getEstablecimiento());
		ptoemisionM.setDireccion(ptoemisionm.getDireccion());
		ptoemisionM.setEstado(ptoemisionm.getEstado());
		ptoemisionM.setTelefono(ptoemisionm.getTelefono());
		ptoemisionM.setNroautorizacion(ptoemisionm.getNroautorizacion());
		ptoemisionM.setUsucrea(ptoemisionm.getUsucrea());
		ptoemisionM.setFeccrea(ptoemisionm.getFeccrea());
		Ptoemision updatePtoEmision = ptoServicio.save(ptoemisionM);
		return ResponseEntity.ok(updatePtoEmision);
	}

	@DeleteMapping(value = "/{idptoemision}")
	private ResponseEntity<Object> deletePtoEmision(@PathVariable ("idptoemision") Long idptoemision){
		ptoServicio.deleteById(idptoemision);
		return ResponseEntity.ok(Boolean.TRUE);
	}
	
}
