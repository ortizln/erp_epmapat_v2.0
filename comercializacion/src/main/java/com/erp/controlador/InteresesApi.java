package com.erp.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.erp.servicio.TmpinteresxfacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.excepciones.ResourceNotFoundExcepciones;
import com.erp.modelo.Intereses;
import com.erp.servicio.InteresServicio;

@RestController
@RequestMapping("/intereses")


public class InteresesApi {

	@Autowired
	private InteresServicio inteServicio;
    @Autowired
    private TmpinteresxfacService tmpService;

    @GetMapping
	public List<Intereses> getAllLista(@Param(value = "anio") Number anio, @Param(value = "mes") Number mes) {
		if (anio != null && mes != null) {
			return inteServicio.findByAnioMes(anio, mes);
		} else {
			return inteServicio.findAll(Sort.by(Sort.Order.desc("anio"), Sort.Order.desc("mes")));
		}
	}

	// Busca el último
	@GetMapping("/ultimo")
	public List<Intereses> getUltimo() {
		return inteServicio.findUltimo();
	}

	@PostMapping
	public ResponseEntity<Intereses> saveIntereses(@RequestBody Intereses x) {
		return ResponseEntity.ok(inteServicio.save(x));
	}

	@GetMapping("/{idinteres}")
	public ResponseEntity<Intereses> getByIdIntereses(@PathVariable Long idinteres) {
		Intereses interesesM = inteServicio.findById(idinteres)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe ese interes con ese Id: " + idinteres));
		return ResponseEntity.ok(interesesM);
	}

	@PutMapping("/{idinteres}")
	public ResponseEntity<Intereses> updateIntereses(@PathVariable Long idinteres, @RequestBody Intereses interesM) {
		Intereses interesesM = inteServicio.findById(idinteres)
				.orElseThrow(() -> new ResourceNotFoundExcepciones("No existe ese interes con ese Id: " + idinteres));
		interesesM.setAnio(interesM.getAnio());
		interesesM.setMes(interesM.getMes());
		interesesM.setPorcentaje(interesM.getPorcentaje());
		interesesM.setUsucrea(interesM.getUsucrea());
		interesesM.setFeccrea(interesM.getFeccrea());
		interesesM.setUsumodi(interesM.getUsumodi());
		interesesM.setFecmodi(interesM.getFecmodi());
		Intereses updatedInteres = inteServicio.save(interesesM);
		return ResponseEntity.ok(updatedInteres);
	}

	@DeleteMapping(value = "/{idinteres}")
	public ResponseEntity<Boolean> deleteInteres(@PathVariable("idinteres") Long idinteres) {
		inteServicio.deleteById(idinteres);
		return ResponseEntity.ok(!(inteServicio.findById(idinteres) != null));
	}

	@GetMapping("/calcular")
	public ResponseEntity<Object> calcularIntereses(@RequestParam Long idfactura) {
		return ResponseEntity.ok(inteServicio.facturaid(idfactura));
	}

    /**
    * ===============================================================================================================
    */

    /**
     * 1) Ejecuta el proceso batch (upsert) de intereses en TMP para todas las facturas “sin cobrar”.
     *    POST /api/intereses/tmp/recalcular
     */
    @PostMapping("/tmp/recalcular")
    public ResponseEntity<Map<String, Object>> recalcularTmp() {
        Map<String, Object> resp = tmpService.updateTmpInteresxfac();
        return ResponseEntity.ok(resp);
    }

    /**
     * 2) Calcula (solo vista previa) el interés de una factura específica al día indicado (o hoy por defecto).
     *    GET /api/intereses/facturas/{id}/calculo?fecha=2025-10-20
     */
    @GetMapping("/facturas/{id}/calculo")
    public ResponseEntity<Map<String, Object>> calcularInteresPorFactura(
            @PathVariable("id") Long idFactura,
            @RequestParam(value = "fecha", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha
    ) {
        LocalDate corte = (fecha != null) ? fecha : LocalDate.now();
        BigDecimal interes = inteServicio.calcularInteresFactura(idFactura, corte);

        Map<String, Object> body = new HashMap<>();
        body.put("status", 200);
        body.put("idFactura", idFactura);
        body.put("fechaCorte", corte);
        body.put("interesCalculado", interes);
        return ResponseEntity.ok(body);
    }

    /**
     * 3) (Opcional) Expone porcentajes por rango YearMonth para auditoría o UI.
     *    GET /api/intereses/porcentajes?desde=2024-01&hasta=2025-09
     */
    @GetMapping("/porcentajes")
    public ResponseEntity<Map<String, Object>> getPorcentajesRango(
            @RequestParam("desde") String desde,  // yyyy-MM
            @RequestParam("hasta") String hasta   // yyyy-MM
    ) {
        YearMonth ymDesde;
        YearMonth ymHasta;
        try {
            ymDesde = YearMonth.parse(desde);
            ymHasta = YearMonth.parse(hasta);
        } catch (DateTimeParseException e) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", 400);
            err.put("message", "Parámetros inválidos. Usa formato yyyy-MM, ej: 2025-01");
            return ResponseEntity.badRequest().body(err);
        }

        if (ymDesde.isAfter(ymHasta)) {
            Map<String, Object> err = new HashMap<>();
            err.put("status", 400);
            err.put("message", "'desde' no puede ser posterior a 'hasta'");
            return ResponseEntity.badRequest().body(err);
        }

        // Si creaste el InteresRepositoryAdapter con getPorcentajesPorRango(...)
        var map = inteServicio
                .getRepoAdapter() // expón un getter o inyecta aquí el adapter directamente
                .getPorcentajesPorRango(ymDesde, ymHasta);

        Map<String, Object> ok = new HashMap<>();
        ok.put("status", 200);
        ok.put("desde", ymDesde.toString());
        ok.put("hasta", ymHasta.toString());
        ok.put("porcentajes", map); // { "2024-01": 1.25, ... } si lo serializas como Map<String, BigDecimal>
        return ResponseEntity.ok(ok);
    }

}
