package com.erp.comercializacion.controllers;

import java.util.List;
import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Facelectro;
import com.erp.comercializacion.services.FacelectroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facelectro")
@CrossOrigin(origins = "*")
public class FacelectroApi {

	@Autowired
	private FacelectroService faceleServicio;

	@GetMapping
	public List<Facelectro> get(@Param(value = "nrofac") String nrofac,
								@Param(value = "idcliente") Long idcliente) {
		if (nrofac != null)
			return faceleServicio.findByNrofac(nrofac);
		else {
			if (idcliente != null)
				return faceleServicio.findByIdcliente(idcliente);
			return null;
		}
	}

	// Solo para probar en Postman
	// @GetMapping
	// public List<Facelectro> get20() {
	// return faceleServicio.find20();
	// }

	// facelectro.idfacelectro no es clave foranea de ningún Tabla (Se usa solo para
	// probar en Postman )
	@GetMapping("/{idfacelectro}")
	public ResponseEntity<Facelectro> getById(@PathVariable Long idfacelectro) {
		Facelectro x = faceleServicio.findById(idfacelectro)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe la Facelectro Id: " + idfacelectro)));
		return ResponseEntity.ok(x);
	}

}
