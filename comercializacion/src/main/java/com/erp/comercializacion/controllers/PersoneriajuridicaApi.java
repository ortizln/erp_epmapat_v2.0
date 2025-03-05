package com.erp.comercializacion.controllers;

import com.erp.comercializacion.models.PersonJuridica;
import com.erp.comercializacion.services.PersonJuridicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/personeriajuridica")
@CrossOrigin("*")
public class PersoneriajuridicaApi
{
    @Autowired
    private PersonJuridicaService pjService;

    @GetMapping
    public List<PersonJuridica> getAllPersoneriaJuridica(){
        return pjService.findAll();
    }

    @PostMapping
    public PersonJuridica updateOrSave(@RequestBody PersonJuridica pj) {
        return pjService.save(pj);
    }
}
