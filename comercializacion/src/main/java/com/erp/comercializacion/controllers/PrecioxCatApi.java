package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Precioxcat;
import com.erp.comercializacion.services.PrecioxcatService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/pliego")
@CrossOrigin(origins = "*")
public class PrecioxCatApi {

	@Autowired
	private PrecioxcatService pxcatServicio;

	@GetMapping
	public List<Precioxcat> getAllPreciosxCat(@Param(value = "categoria") Long categoria,
											  @Param(value = "dm3") Long dm3, @Param(value = "hm3") Long hm3) {
		return pxcatServicio.findAll(categoria, dm3, hm3);
	}

	@GetMapping("/{idprecioxcat}")
	public ResponseEntity<Precioxcat> getByIdPrecioxCat(@PathVariable Long idprecioxcat) {
		Precioxcat precioxcatM = pxcatServicio.findById(idprecioxcat).orElseThrow(() -> new ResourceNotFoundExcepciones(
				"No se Encuentra el precio x categoria con ese ID: " + idprecioxcat));
		return ResponseEntity.ok(precioxcatM);
	}
	
	@GetMapping("/consumo")
	public List<Precioxcat> getConsumo(@Param(value = "idcategoria") Long idcategoria,
			@Param(value = "m3") Long m3) {
		return pxcatServicio.findConsumo(idcategoria, m3 );
	}

	@PostMapping
	public Precioxcat savePrecioxCat(@RequestBody Precioxcat precioxcatM) {
		return pxcatServicio.save(precioxcatM);
	}

	@PutMapping("/{idprecioxcat}")
	public ResponseEntity<Precioxcat> updatedPrecioxCat(@PathVariable Long idprecioxcat,
			@RequestBody Precioxcat precioxcatm) {
		Precioxcat precioxcatM = pxcatServicio.findById(idprecioxcat).orElseThrow(() -> new ResourceNotFoundExcepciones(
				"No se Encuentra ese precio x categoria con ese ID: " + idprecioxcat));
		precioxcatM.setM3(precioxcatm.getM3());
		precioxcatM.setPreciobase(precioxcatm.getPreciobase());
		precioxcatM.setPrecioadicional(precioxcatm.getPrecioadicional());
		precioxcatM.setIdcategoria_categorias(precioxcatm.getIdcategoria_categorias());
		precioxcatM.setUsucrea(precioxcatm.getUsucrea());
		precioxcatM.setFeccrea(precioxcatm.getFeccrea());
		precioxcatM.setUsumodi(precioxcatm.getUsumodi());
		precioxcatM.setFecmodi(precioxcatm.getFecmodi());
		Precioxcat updatePrecioxCat = pxcatServicio.save(precioxcatM);
		return ResponseEntity.ok(updatePrecioxCat);
	}

	@DeleteMapping("/{idprecioxcat}")
	public ResponseEntity<Boolean> deletePrecioxCat(@PathVariable("idprecioxcat") Long idprecioxcat) {
		pxcatServicio.deleteById(idprecioxcat);
		return ResponseEntity.ok(!(pxcatServicio.findById(idprecioxcat) != null));
	}

}
