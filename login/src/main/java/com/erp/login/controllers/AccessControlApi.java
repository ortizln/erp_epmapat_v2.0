package com.erp.login.controllers;

import com.erp.login.services.AccessControlService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/access")
public class AccessControlApi {

    private final AccessControlService service;

    public AccessControlApi(AccessControlService service) {
        this.service = service;
    }

    @GetMapping("/profile")
    public ResponseEntity<List<Map<String, Object>>> profile(@RequestParam Long idusuario,
                                                             @RequestParam(required = false) String platform) {
        return ResponseEntity.ok(service.getProfile(idusuario, platform));
    }

    @PostMapping("/sections")
    public ResponseEntity<Map<String, Object>> saveSection(@RequestBody Map<String, Object> body) {
        Long idusuario = body.get("idusuario") == null ? null : Long.valueOf(String.valueOf(body.get("idusuario")));
        Long idseccion = body.get("iderpseccion") == null ? null : Long.valueOf(String.valueOf(body.get("iderpseccion")));
        Boolean enabled = body.get("enabled") != null && Boolean.parseBoolean(String.valueOf(body.get("enabled")));

        if (idusuario == null || idseccion == null) {
            return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "idusuario e iderpseccion son requeridos"));
        }

        int n = service.saveUserSection(idusuario, idseccion, enabled);
        return ResponseEntity.ok(Map.of("ok", n > 0));
    }
}
