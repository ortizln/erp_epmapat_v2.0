package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.models.Rubroadicional;
import com.erp.comercializacion.services.RubroadicionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rubroadicional")
@CrossOrigin(origins = "*")

public class RubroAdicionalApi {

	@Autowired
	private RubroadicionalService rubadiServicio;

	@GetMapping
	public List<Rubroadicional> getAllRubrosAdicional(){
		return rubadiServicio.findAll();
	}

	@GetMapping("/tptramite/{idtptramite}")
	public List<Rubroadicional> getRubAdiByIdTpTramite(@PathVariable Long idtptramite){
		return rubadiServicio.findByIdTpTramtie(idtptramite);
	}

}
