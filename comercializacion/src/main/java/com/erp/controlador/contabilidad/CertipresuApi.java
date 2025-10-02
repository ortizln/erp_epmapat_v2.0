package com.erp.controlador.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
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

import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.modelo.contabilidad.Certipresu;
import com.erp.servicio.contabilidad.CertipresuServicio;

@RestController
@RequestMapping("/certipresu")
@CrossOrigin("*")

public class CertipresuApi {

	@Autowired
	private CertipresuServicio certiServicio;

	@GetMapping
	public List<Certipresu> getAllLista(@Param(value = "desdeNum") Long desdeNum,
			@Param(value = "hastaNum") Long hastaNum,
			@Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
			@Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
		if (desdeNum != null) {
			return certiServicio.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
		} else
			return null;
	}

	@GetMapping("/ultimo")
	public Certipresu ultimo() {
		return certiServicio.findFirstByOrderByNumeroDesc();
	}

	//Valida número certificación (OJO preferibre debería retornar true/false y con Param)
	@GetMapping("/numero/{numero}/tipo/{tipo}")
	public Certipresu valNumero(@PathVariable Long numero, @PathVariable Integer tipo) {
		return certiServicio.findByNumeroAndTipo(numero, tipo);
	}

	//Una certificación por número
	@GetMapping("/numero")
	public Certipresu findCertipresu(@Param(value = "numero") Long numero, @Param(value = "tipo") Integer tipo ) {
		return certiServicio.findByNumeroAndTipo(numero, tipo);
	}

	@PostMapping
	public Certipresu saveCertiPresu(@RequestBody Certipresu certipresu) {
		return certiServicio.save(certipresu);
	}

	@PutMapping("/{idcerti}")
	public ResponseEntity<Certipresu> updateCertiPresu(@PathVariable Long idcerti, @RequestBody Certipresu y) {
		Certipresu x = certiServicio.findById(idcerti)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idcerti));
		x.setTipo(y.getTipo());
		x.setNumero(y.getNumero());
		x.setFecha(y.getFecha());
		x.setValor(y.getValor());
		x.setDescripcion(y.getDescripcion());
		x.setNumdoc(y.getNumdoc());
		x.setUsucrea(y.getUsucrea());
		x.setFeccrea(y.getFeccrea());
		x.setUsumodi(y.getUsumodi());
		x.setFecmodi(y.getFecmodi());
		x.setIdbene(y.getIdbene());
		x.setIdbeneres(y.getIdbeneres());
		x.setIntdoc(y.getIntdoc());
		Certipresu updateCerti = certiServicio.save(x);
		return ResponseEntity.ok(updateCerti);
	}

	@GetMapping("/{idcerti}")
	public Optional<Certipresu> findByIdCertiPresu(@PathVariable Long idcerti) {
		return certiServicio.findById(idcerti);
	}

	@DeleteMapping(value = "/{idcerti}")
	public ResponseEntity<Boolean> deleteCertipresu(@PathVariable("idcerti") Long idcerti) {
		certiServicio.deleteById(idcerti);
		return ResponseEntity.ok(!(certiServicio.findById(idcerti) != null));
	}

}
