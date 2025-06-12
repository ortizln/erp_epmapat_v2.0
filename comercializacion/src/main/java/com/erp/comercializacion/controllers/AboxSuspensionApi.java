package com.erp.comercializacion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.comercializacion.models.Abonadosxsuspension;
import com.erp.comercializacion.services.AbonadosxsuspensionService;

@RestController
@RequestMapping("/aboxsuspension")
@CrossOrigin(origins = "*")
public class AboxSuspensionApi {

	@Autowired
	private AbonadosxsuspensionService aboxsuspensionS;

	@GetMapping
	public List<Abonadosxsuspension> getAllAboxSuspension(){
		return aboxsuspensionS.findAll();
	}

	@PostMapping
	public Abonadosxsuspension saveAboxSuspension(@RequestBody Abonadosxsuspension aboxsuspensionM) {
		return aboxsuspensionS.save(aboxsuspensionM);
	}

	@GetMapping("suspension/{idsuspension}")
	public List<Abonadosxsuspension> getByIdsuspension(@PathVariable Long idsuspension){
		return aboxsuspensionS.findByIdsuspension(idsuspension);
	}
}
