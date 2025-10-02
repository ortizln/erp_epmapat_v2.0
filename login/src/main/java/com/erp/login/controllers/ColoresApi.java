package com.erp.login.controllers;

import com.erp.login.models.Colores;
import com.erp.login.services.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/colores")
@CrossOrigin("*")
public class ColoresApi {
    @Autowired
    private ColorService coloServicio;

    @GetMapping("/tonos")
    public List<Colores> findTonos() {
        return coloServicio.findTonos();
    }

    @GetMapping
    public List<Colores> findByTono(@Param(value = "codigo") String codigo) {
        if (codigo != null) {
            return coloServicio.findByTono(codigo);
        } else {
            return null;
        }
    }
}
