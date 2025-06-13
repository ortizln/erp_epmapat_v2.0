package com.erp.comercializacion.controllers;
import java.util.List;

import com.erp.comercializacion.excepciones.ResourceNotFoundExcepciones;
import com.erp.comercializacion.models.Tptramite;
import com.erp.comercializacion.services.TptramiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tptramite")
@CrossOrigin("*")

public class TpTramiteApi {

	@Autowired
	private TptramiteService tptramiServicio;

	@GetMapping
	public List<Tptramite> getAllTpTramite(){
		return tptramiServicio.findAll();
	}
	
	@GetMapping("/{idtptramite}")
	public ResponseEntity<Tptramite> findByIdTptramite(@PathVariable Long idtptramite){
		Tptramite tptramiteM = tptramiServicio.findById(idtptramite)
				.orElseThrow(()-> new ResourceNotFoundExcepciones("No se encuentra este tptramtie con este id: "+idtptramite));
		return ResponseEntity.ok(tptramiteM);
	}

}
