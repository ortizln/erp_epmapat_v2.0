package com.erp.controlador.contabilidad;

import java.util.List;

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

import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.modelo.contabilidad.Cuentas;
import com.erp.servicio.contabilidad.CuentaServicio;

@RestController
@RequestMapping("/cuentas")
@CrossOrigin("*")

public class CuentasApi {

	@Autowired
	private CuentaServicio cueServicio;

	@GetMapping
	public List<Cuentas> getAllLista(
			@Param(value = "nomcue") String nomcue,
			@Param(value = "codcue") String codcue,
			@Param(value = "grucue") String grucue,
			@Param(value = "asohaber") String asohaber,
			@Param(value = "asodebe") String asodebe) {

		if (codcue != null) {
			return cueServicio.findByCodcue(codcue);
		} else {
			if (nomcue != null) {
				return cueServicio.findByNomcue(nomcue.toLowerCase());
			} else {
				if (grucue != null) {
					return cueServicio.findByGrucue(grucue);
				} else {
					if (asohaber != null) {
						return cueServicio.findByAsohaber(asohaber);
					} else {
						if (asodebe != null) {
							return cueServicio.findByAsodebe(asodebe);
						} else
							return cueServicio.findAll();
					}
				}
			}
		}
	}

	// Lista de cuentas por codigo y/o nombre
	@GetMapping("/lista")
	public List<Cuentas> getAllLista(@Param(value = "codcue") String codcue,
			@Param(value = "nomcue") String nomcue) {
		if (codcue != null && nomcue != null) {
			return cueServicio.findByCodigoyNombre(codcue, nomcue.toLowerCase());
		} else
			return null;
	}

	@GetMapping("/bancos")
	public ResponseEntity<List<Cuentas>> getBancos() {
		return ResponseEntity.ok(cueServicio.findBancos());
	}

	// Cuentas por Tiptran para los DataList de Cuentas
	@GetMapping("/porTiptran")
	public List<Cuentas> getByTiptran(@Param(value = "tiptran") Integer tiptran,
			@Param(value = "codcue") String codcue) {
		return cueServicio.findByTiptran(tiptran, codcue+'%');
	}

	@GetMapping("/nombre/{codcue}")
	public Object[] getNombre(@PathVariable("codcue") String codcue) {
		return cueServicio.getNomCueByCodcue(codcue);
	}

	// Valida codcue
	@GetMapping("/valcodcue")
	public ResponseEntity<Boolean> valCodcue(@Param(value = "codcue") String codcue) {
		boolean rtn = cueServicio.valCodcue(codcue);
		return ResponseEntity.ok(rtn);
	}

	// Valida el nombre de la Cuenta
	@GetMapping("/valnomcue")
	public ResponseEntity<Boolean> valNomcue(@Param(value = "nomcue") String nomcue) {
		boolean rtn = cueServicio.valNomcue(nomcue.toLowerCase());
		return ResponseEntity.ok(rtn);
	}

	// Verifica si tiene Desagregación
	@GetMapping("/desagrega")
	public ResponseEntity<Boolean> valDesagrega(@Param(value = "codcue") String codcue,
			@Param(value = "nivcue") Integer nivcue) {
		boolean rtn = cueServicio.valDesagrega(codcue, nivcue);
		return ResponseEntity.ok(rtn);
	}

	// Busca una cuenta por codcue
	@GetMapping("/detalle")
	public Cuentas getDetalle(@Param(value = "codcue") String codcue) {
		return cueServicio.findCuentasByCodcue(codcue);
	}

	// Cuentas de costos
	@GetMapping("/cuecostos")
	public List<Cuentas> findCuecostos() {
		return cueServicio.findCuecostos();
	}

	@GetMapping("/{idcuenta}")
	public ResponseEntity<Cuentas> getByIddocumento(@PathVariable Long idcuenta) {
		Cuentas documento = cueServicio.findById(idcuenta)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe el Cuenta con Id: " + idcuenta)));
		return ResponseEntity.ok(documento);
	}

	@PostMapping
	public ResponseEntity<Cuentas> save(@RequestBody Cuentas x) {
		return ResponseEntity.ok(cueServicio.save(x));
	}

	@PutMapping("/{idcuenta}")
	public ResponseEntity<Cuentas> update(@PathVariable Long idcuenta, @RequestBody Cuentas x) {
		Cuentas y = cueServicio.findById(idcuenta)
				.orElseThrow(() -> new ResourceNotFoundExcepciones(
						("No existe Cuenta con Id: " + idcuenta)));
		y.setCodcue(x.getCodcue());
		y.setNomcue(x.getNomcue());
		y.setGrucue(x.getGrucue());
		// y.setIdnivel(x.getIdnivel());
		y.setMovcue(x.getMovcue());
		y.setAsodebe(x.getAsodebe());
		y.setAsohaber(x.getAsohaber());
		y.setDebito(x.getDebito());
		y.setCredito(x.getCredito());
		y.setSaldo(x.getSaldo());
		y.setBalance(x.getBalance());
		y.setIntgrupo(x.getIntgrupo());
		y.setSigef(y.getSigef());
		y.setTiptran(x.getTiptran());
		y.setUsucrea(x.getUsucrea());
		y.setFeccrea(x.getFeccrea());
		y.setUsumodi(x.getUsumodi());
		y.setFecmodi(x.getFecmodi());
		y.setGrufluefec(x.getGrufluefec());
		y.setResulcostos(x.getResulcostos());
		y.setBalancostos(x.getBalancostos());

		Cuentas actualizar = cueServicio.save(y);
		return ResponseEntity.ok(actualizar);
	}

	@DeleteMapping("/{idcuenta}")
	private ResponseEntity<Boolean> deleteCuenta(@PathVariable("idcuenta") Long idcuenta) {
		cueServicio.deleteById(idcuenta);
		return ResponseEntity.ok(!(cueServicio.findById(idcuenta) != null));
	}

}
