package com.erp.comercializacion.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.comercializacion.models.PersonJuridica;
import com.erp.comercializacion.services.PersonJuridicaServicio;

@RestController
@RequestMapping("/personeriajuridica")
@CrossOrigin(origins = "*")

public class PersoneriaJuridicaC {

	@Autowired
	private PersonJuridicaServicio pjService;

	@GetMapping
	public List<PersonJuridica> getAllPersoneriaJuridica(){
		return pjService.findAll();
	}
	
	@PostMapping
    public PersonJuridica updateOrSave(@RequestBody PersonJuridica pj) {
        return pjService.save(pj);
    }
}
