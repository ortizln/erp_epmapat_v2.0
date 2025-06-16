package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Rubros;
import com.erp.comercializacion.services.RubrosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/rubros")
@CrossOrigin("*")
public class RubrosApi {

	@Autowired
	private RubrosService rubServicio;

	@GetMapping
	public List<Rubros> getAllLista(@Param(value = "idmodulo") Long idmodulo,
									@Param(value = "descripcion") String descripcion) {
		if (idmodulo != null && descripcion != null) {
			System.out.println("FIND");
			return rubServicio.findByNombre(idmodulo, descripcion.toLowerCase());
		} else {
			System.out.println("FIND ALL RUBROS");
			return rubServicio.findAll();
		}
	}

	// Rubros por módulo (Sección)
	@GetMapping("/modulo/{idmodulo}")
	public List<Rubros> findByIdmodulo(@PathVariable Long idmodulo) {
		System.out.println("FIND MÓDULOS");

		return rubServicio.findByIdmodulo(idmodulo);
	}

	@GetMapping("/{idrubro}")
	public ResponseEntity<Rubros> getByIdRubros(@PathVariable("idrubro") Long idrubro) {
		Rubros rubros = rubServicio.findById(idrubro)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe el rubro con Id: " + idrubro));
		System.out.println("FIND BY ID_RUBRO");

		return ResponseEntity.ok(rubros);
	}

	// Por Módulo y Descripción
	@GetMapping("/idmodulo")
	public List<Rubros> valNombre(@Param(value = "idmodulo") Long idmodulo,
			@Param(value = "descripcion") String descripcion) {
		if (idmodulo != null && descripcion != null) {
			return rubServicio.findByModulo(idmodulo, descripcion.toLowerCase());
		} else
			return null;
	}

	// Rubros de la Emisión (idmodulo=4)
	@GetMapping("/emision")
	public List<Rubros> findEmision() {
		return rubServicio.findEmision();
	}

	@PostMapping
	public ResponseEntity<Rubros> save(@RequestBody Rubros rubros) {
		return ResponseEntity.ok(rubServicio.save(rubros));
	}

	@PutMapping("/update")
	public ResponseEntity<Rubros> update(@RequestParam Long idrubro, @RequestBody Rubros x) {
		System.out.println(idrubro);
		Rubros y = rubServicio.findById(idrubro)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe el Rubro con Id: " + idrubro)));
		y.setIdmodulo_modulos(x.getIdmodulo_modulos());
		y.setDescripcion(x.getDescripcion());
		y.setEstado(x.getEstado());
		y.setValor(x.getValor());
		y.setCalculable(x.getCalculable());
		y.setSwiva(x.getSwiva());
		y.setTipo(x.getTipo());
		y.setFacturable(x.getFacturable());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());

		Rubros actualizar = rubServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}

	@GetMapping("/findByName")
	public ResponseEntity<List<Rubros>> getByName(@RequestParam String descripcion) {
		return ResponseEntity.ok(rubServicio.findByName(descripcion.toLowerCase()));

	}

}
