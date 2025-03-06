package com.erp.login.controllers;

import com.erp.login.interfaces.ErpModulosI;
import com.erp.login.models.Usrxmodulos;
import com.erp.login.services.UsrxmodulosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usrxmodulos")
@CrossOrigin("*")
public class UsrModulosApi {
    @Autowired
    private UsrxmodulosService umServicio;

    @GetMapping("/access")
    public ResponseEntity<List<ErpModulosI>> getModulosEnabledByUser(@RequestParam Long idusuario) {
        System.out.println(idusuario);
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
