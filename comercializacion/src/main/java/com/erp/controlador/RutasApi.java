package com.erp.controlador;

import java.util.List;

import com.erp.interfaces.RutasInterfaces;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.interfaces.CuentasByRutas;
import com.erp.modelo.Rutas;
import com.erp.servicio.RutaServicio;

@RestController
@RequestMapping("/api/rutas")


public class RutasApi {

	@Autowired
	private RutaServicio rutServicio;

	@GetMapping
	public List<Rutas> getAll() {
		return rutServicio.findAllActive();
	}

	@GetMapping("/valCodigo")
	public ResponseEntity<Boolean> valCodigo(@Param(value = "codigo") String codigo) {
		boolean rtn = rutServicio.valCodigo(codigo);
		return ResponseEntity.ok(rtn);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Rutas saveRutas(@RequestBody Rutas x) {
		return rutServicio.save(x);
	}

	@GetMapping("/{idruta}")
	public ResponseEntity<Rutas> getByIdRutas(@PathVariable Long idruta) {
		Rutas rutasM = rutServicio.findById(idruta)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe la Ruta con ID: " + idruta));
		return ResponseEntity.ok(rutasM);
	}

	@PutMapping(value = "/{idruta}")
	public ResponseEntity<Rutas> updateRutas(@PathVariable Long idruta, @RequestBody Rutas rutasm) {
		Rutas rutasM = rutServicio.findById(idruta)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe la Ruta con ID: " + idruta));
		rutasM.setDescripcion(rutasm.getDescripcion());
		rutasM.setOrden(rutasm.getOrden());
		rutasM.setUsucrea(rutasm.getUsucrea());
		rutasM.setCodigo(rutasm.getCodigo());
		rutasM.setFeccrea(rutasm.getFeccrea());
		rutasM.setUsumodi(rutasm.getUsumodi());
		rutasM.setFecmodi(rutasm.getFecmodi());
		Rutas updateRutas = rutServicio.save(rutasM);
		return ResponseEntity.ok(updateRutas);
	}

	@DeleteMapping(value = "/{idruta}")
	public ResponseEntity<Boolean> delteRuta(@PathVariable("idruta") Long idruta) {
		rutServicio.deleteById(idruta);
		return ResponseEntity.ok(!(rutServicio.findById(idruta) != null));
	}

	@GetMapping("/cuentasByRuta")
	public ResponseEntity<List<CuentasByRutas>> getNcuentasByRutas() {
		return ResponseEntity.ok(rutServicio.getNcuentasByRutas());
	}
    @GetMapping("/deudas_ruta_cuentas")
    public ResponseEntity<List<RutasInterfaces>> getDeudaOfCuentasByIdRutas(@RequestParam Long idruta){
        return ResponseEntity.ok(rutServicio.getDeudaOfCuentasByIdrutas(idruta));
    }
    @GetMapping("/deudas_cuentas")
    public ResponseEntity<List<RutasInterfaces>> getDeudasOfAllCuentas(){
        return ResponseEntity.ok(rutServicio.getDeudasOfAllCuentas());
    }
    @GetMapping("/deudas_rutas")
    public ResponseEntity<List<RutasInterfaces>> getDeudasOfRutas(){
        return ResponseEntity.ok(rutServicio.getDeudasOfRutas());
    }

}
