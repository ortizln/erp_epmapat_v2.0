package com.erp.controlador.coactivas;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.erp.modelo.coactivas.Facxremi;
import com.erp.servicio.coactivas.FacxremiServicio;

@RestController
@RequestMapping("/facxremi")

public class FacxremiApi {
    @Autowired
    private FacxremiServicio facxremiServicio;

    @PostMapping
    public ResponseEntity<Facxremi> save(@RequestBody Facxremi facxremi) {
        return ResponseEntity.ok(facxremiServicio.savefacxremi(facxremi));
    }

    @GetMapping("/byremision")
    public ResponseEntity<List<Facxremi>> getByRemision(@RequestParam Long idremision, @RequestParam Long tipfac) {
        return ResponseEntity.ok(facxremiServicio.findByRemision(idremision, tipfac));
    }

}
