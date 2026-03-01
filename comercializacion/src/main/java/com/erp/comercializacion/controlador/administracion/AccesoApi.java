package com.erp.comercializacion.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.erp.comercializacion.modelo.administracion.Acceso;
import com.erp.comercializacion.servicio.administracion.AccesoServicio;

@RestController
@RequestMapping("/api/acceso")
public class AccesoApi {

    @Autowired
    AccesoServicio accServicio;

    @GetMapping
    public List<Acceso> getAll() {
        return accServicio.findAll();
    }

}

