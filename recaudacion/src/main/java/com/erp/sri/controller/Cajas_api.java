package com.erp.sri.controller;

import com.erp.sri.service.Cajas_ser;
import com.erp.sri.service.Usuario_ser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rec/cajas")
@CrossOrigin("*")
public class Cajas_api {
    @Autowired
    private Cajas_ser s_cajas;
    @Autowired
    private Usuario_ser s_user;

    @GetMapping("/singin")
    public ResponseEntity<Object> cajasSingIn(@RequestParam String username, @RequestParam String password){

        try {
            Object usuario = s_cajas.cajasSingIn(username, password);
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    @PutMapping("/singout")
    public ResponseEntity<Object> cajasSingOut(@RequestParam String username){
        System.out.println(username);
        return ResponseEntity.ok(s_cajas.cajasSingOut(username));

    }
    @GetMapping("/decode")
    public ResponseEntity<String> decode(@RequestParam String username){
        return ResponseEntity.ok(s_user.decodificar(username));

    }
    @GetMapping("/encode")
    public ResponseEntity<String> encode(@RequestParam String username){
        return ResponseEntity.ok(s_user.codificar(username));

    }
    @GetMapping("/test_connection")
    public ResponseEntity<Object> testContection(@RequestParam Long user){
        return ResponseEntity.ok(s_cajas.testIfLogin(user));
    }


}
