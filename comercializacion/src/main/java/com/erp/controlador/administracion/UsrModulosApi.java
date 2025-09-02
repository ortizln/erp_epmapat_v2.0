package com.erp.controlador.administracion;

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

import com.erp.interfaces.ErpModulosI;
import com.erp.modelo.administracion.Usrxmodulos;
import com.erp.servicio.administracion.UsrxmodulosServicio;

@RestController
@RequestMapping("/usrxmodulos")
@CrossOrigin("*")
public class UsrModulosApi {
    @Autowired
    private UsrxmodulosServicio umServicio;

    @GetMapping("/access")
    public ResponseEntity<List<ErpModulosI>> getModulosEnabledByUser(@RequestParam Long idusuario) {
        return ResponseEntity.ok(umServicio.findModulosEnabledByUser(idusuario));
    }

    @GetMapping
    public ResponseEntity<List<Usrxmodulos>> getAllByUser(@RequestParam Long idusuario) {
        return ResponseEntity.ok(umServicio.FindByUser(idusuario));
    }

    @PostMapping
    public ResponseEntity<Usrxmodulos> save(@RequestBody Usrxmodulos usrxmodulos) {
        return ResponseEntity.ok(umServicio.save(usrxmodulos));
    }
}
