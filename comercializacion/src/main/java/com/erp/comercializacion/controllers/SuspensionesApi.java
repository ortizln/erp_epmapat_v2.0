package com.erp.comercializacion.controllers;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.erp.comercializacion.models.Suspensiones;
import com.erp.comercializacion.services.SuspensionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/suspensiones")
@CrossOrigin("*")

public class SuspensionesApi {

	@Autowired
	private SuspensionesService suspensionesS;

	@GetMapping
	public List<Suspensiones> getAllSuspensiones(){
		return suspensionesS.findAll();
	}
	
	@PostMapping
	public Suspensiones saveSuspencion(@RequestBody Suspensiones suspensionesM) {
		return suspensionesS.save(suspensionesM);
	}
	@GetMapping("/susp/{desde}/{hasta}")
	public List<Suspensiones> getByFecha(@PathVariable String desde, @PathVariable String hasta){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		Date h = null;		
		try {
			d = (Date) dateFormat.parse(desde);
			h = (Date) dateFormat.parse(hasta);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return suspensionesS.findByFecha(d, h);
	}
	@GetMapping("/habbydate/{desde}/{hasta}")
	public List<Suspensiones> getByFechaHabilitacion(@PathVariable String desde, @PathVariable String hasta){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date d = null;
		Date h = null;
		try {
			d = (Date) dateFormat.parse(desde);
			h = (Date) dateFormat.parse(hasta);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return suspensionesS.findByFechaHabilitaciones(d, h);
	}
	@GetMapping("/susp/{numero}")
	public List<Suspensiones> getByNumero(@PathVariable Long numero){
		return suspensionesS.findByNumero(numero);
	}
	@GetMapping("/habilitaciones")
	public List<Suspensiones> getByHabilitaciones(){
		return suspensionesS.findHabilitaciones();
	}
	@GetMapping("/ultimo")
	public Suspensiones getUltima(){
		return suspensionesS.findFirstByOrderByIdsuspensionDesc();
	}

}
