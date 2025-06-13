package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.models.Liquidatramite;
import com.erp.comercializacion.services.LiquidaTramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/liquidatramite")
@CrossOrigin(origins = "*")
public class LiquidaTramiteApi {

	@Autowired
	private LiquidaTramiteService liqtramiServicio;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<Liquidatramite> getAllLiquidaTramites(){
		return liqtramiServicio.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Liquidatramite saveLiquidaTramite(@RequestBody Liquidatramite liquidatramiteM) {
		return liqtramiServicio.save(liquidatramiteM);
	}

	@GetMapping("/idtramite/{idtramite}")
	public List<Liquidatramite> getlitramiteById(@PathVariable Long idtramite){
		return liqtramiServicio.findByIdTramite(idtramite);
	}

}
