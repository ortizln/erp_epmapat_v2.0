package com.erp.comercializacion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.TpTramiteM;
import com.erp.comercializacion.services.TpTramiteS;

@RestController
@RequestMapping("/tptramite")
@CrossOrigin("*")

public class TpTramiteC {

	@Autowired
	private TpTramiteS tptramiServicio;

	@GetMapping
	public List<TpTramiteM> getAllTpTramite(){
		return tptramiServicio.findAll();
	}
	
	@GetMapping("/{idtptramite}")
	public ResponseEntity<TpTramiteM> findByIdTptramite(@PathVariable Long idtptramite){
		TpTramiteM tptramiteM = tptramiServicio.findById(idtptramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No se encuentra este tptramtie con este id: "+idtptramite));
		return ResponseEntity.ok(tptramiteM);
	}

}
