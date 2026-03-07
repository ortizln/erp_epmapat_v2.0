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

    @GetMapping("/sections/catalog")
    public ResponseEntity<List<Map<String, Object>>> sectionCatalog(@RequestParam Long iderpmodulo,
                                                                    @RequestParam(required = false) String platform) {
        return ResponseEntity.ok(service.sectionCatalog(iderpmodulo, platform));
    }

    @PostMapping("/sections/catalog")
    public ResponseEntity<Map<String, Object>> createSectionCatalog(@RequestBody Map<String, Object> body) {
        Long iderpmodulo = body.get("iderpmodulo") == null ? null : Long.valueOf(String.valueOf(body.get("iderpmodulo")));
        if (iderpmodulo == null) return ResponseEntity.badRequest().body(Map.of("ok", false, "message", "iderpmodulo requerido"));
        int n = service.saveSectionCatalog(
                iderpmodulo,
                String.valueOf(body.getOrDefault("codigo", "")),
                String.valueOf(body.getOrDefault("descripcion", "")),
                body.get("ruta") == null ? null : String.valueOf(body.get("ruta")),
                body.get("orden") == null ? 0 : Integer.valueOf(String.valueOf(body.get("orden"))),
                body.get("platform") == null ? "WEB" : String.valueOf(body.get("platform")),
                body.get("activo") == null ? true : Boolean.valueOf(String.valueOf(body.get("activo"))));
        return ResponseEntity.ok(Map.of("ok", n > 0));
    }

    @PutMapping("/sections/catalog/{iderpseccion}")
    public ResponseEntity<Map<String, Object>> updateSectionCatalog(@PathVariable Long iderpseccion,
                                                                    @RequestBody Map<String, Object> body) {
        int n = service.updateSectionCatalog(
                iderpseccion,
                String.valueOf(body.getOrDefault("codigo", "")),
                String.valueOf(body.getOrDefault("descripcion", "")),
                body.get("ruta") == null ? null : String.valueOf(body.get("ruta")),
                body.get("orden") == null ? null : Integer.valueOf(String.valueOf(body.get("orden"))),
                body.get("platform") == null ? null : String.valueOf(body.get("platform")),
                body.get("activo") == null ? null : Boolean.valueOf(String.valueOf(body.get("activo"))));
        return ResponseEntity.ok(Map.of("ok", n > 0));
    }
}
