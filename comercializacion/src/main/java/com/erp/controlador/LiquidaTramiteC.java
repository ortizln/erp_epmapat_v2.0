package com.erp.controlador;

import java.util.List;

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

import com.erp.modelo.LiquidaTramite;
import com.erp.servicio.LiquidaTramiteS;

@RestController
@RequestMapping("/liquidatramite")


public class LiquidaTramiteC {

	@Autowired
	private LiquidaTramiteS liqtramiServicio;

	@GetMapping
	@ResponseStatus(HttpStatus.OK)
	public List<LiquidaTramite> getAllLiquidaTramites(){
		return liqtramiServicio.findAll();
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public LiquidaTramite saveLiquidaTramite(@RequestBody LiquidaTramite liquidatramiteM) {
		return liqtramiServicio.save(liquidatramiteM);
	}

	@GetMapping("/idtramite/{idtramite}")
	public List<LiquidaTramite> getlitramiteById(@PathVariable Long idtramite){
		return liqtramiServicio.findByIdTramite(idtramite);
	}

}
