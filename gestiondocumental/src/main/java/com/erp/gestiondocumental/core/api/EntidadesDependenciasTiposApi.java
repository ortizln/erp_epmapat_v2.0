package com.erp.gestiondocumental.core.api;

import com.erp.gestiondocumental.core.service.EntidadesDependenciasTiposService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class EntidadesDependenciasTiposApi {

    private final EntidadesDependenciasTiposService service;

    public EntidadesDependenciasTiposApi(EntidadesDependenciasTiposService service) {
        this.service = service;
    }

    @GetMapping({"/entities", "/entidades"})
    public ResponseEntity<?> listEntities(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(service.listEntities(q));
    }

    @PostMapping({"/entities", "/entidades"})
    public ResponseEntity<?> createEntity(@RequestBody Map<String, Object> body) {
        String id = service.createEntity(
                String.valueOf(body.get("codigo")),
                String.valueOf(body.get("nombre")),
                body.get("active") == null || Boolean.parseBoolean(String.valueOf(body.get("active")))
        );
        return ResponseEntity.ok(Map.of("id", id));
    }

    @PutMapping({"/entities/{id}", "/entidades/{id}"})
    public ResponseEntity<?> updateEntity(@PathVariable String id, @RequestBody Map<String, Object> body) {
        int n = service.updateEntity(id, String.valueOf(body.get("codigo")), String.valueOf(body.get("nombre")));
        return n > 0 ? ResponseEntity.ok(Map.of("id", id)) : ResponseEntity.notFound().build();
    }

    @PutMapping({"/entities/{id}/status", "/entidades/{id}/estado"})
    public ResponseEntity<?> setEntityStatus(@PathVariable String id, @RequestBody Map<String, Object> body) {
        boolean active = body.get("active") == null || Boolean.parseBoolean(String.valueOf(body.get("active")));
        int n = service.setEntityStatus(id, active);
        return n > 0 ? ResponseEntity.ok(Map.of("id", id, "active", active)) : ResponseEntity.notFound().build();
    }

    @GetMapping({"/dependencies", "/dependencias"})
    public ResponseEntity<?> listDependencies(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listDependencies(entityCode));
    }

    @PostMapping({"/dependencies", "/dependencias"})
    public ResponseEntity<?> createDependency(@RequestBody Map<String, Object> body) {
        String id = service.createDependency(
                String.valueOf(body.get("entity_code")),
                String.valueOf(body.get("codigo")),
                String.valueOf(body.get("nombre")),
                body.get("padre_id") == null ? null : String.valueOf(body.get("padre_id"))
        );
        return ResponseEntity.ok(Map.of("id", id));
    }

    @PutMapping({"/dependencies/{id}", "/dependencias/{id}"})
    public ResponseEntity<?> updateDependency(@PathVariable String id, @RequestBody Map<String, Object> body) {
        int n = service.updateDependency(
                id,
                String.valueOf(body.get("codigo")),
                String.valueOf(body.get("nombre")),
                body.get("padre_id") == null ? null : String.valueOf(body.get("padre_id"))
        );
        return n > 0 ? ResponseEntity.ok(Map.of("id", id)) : ResponseEntity.notFound().build();
    }

    @PutMapping({"/dependencies/{id}/status", "/dependencias/{id}/estado"})
    public ResponseEntity<?> setDependencyStatus(@PathVariable String id, @RequestBody Map<String, Object> body) {
        boolean active = body.get("active") == null || Boolean.parseBoolean(String.valueOf(body.get("active")));
        int n = service.setDependencyStatus(id, active);
        return n > 0 ? ResponseEntity.ok(Map.of("id", id, "active", active)) : ResponseEntity.notFound().build();
    }

    @GetMapping({"/document-types", "/tipos-documento"})
    public ResponseEntity<?> listDocumentTypes(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.listDocumentTypes(entityCode));
    }

    @PostMapping({"/document-types", "/tipos-documento"})
    public ResponseEntity<?> createDocumentType(@RequestBody Map<String, Object> body) {
        String id = service.createDocumentType(
                String.valueOf(body.get("entity_code")),
                String.valueOf(body.get("codigo")),
                String.valueOf(body.get("nombre")),
                String.valueOf(body.get("flujo")),
                body.get("active") == null || Boolean.parseBoolean(String.valueOf(body.get("active")))
        );
        return ResponseEntity.ok(Map.of("id", id));
    }

    @PutMapping({"/document-types/{id}", "/tipos-documento/{id}"})
    public ResponseEntity<?> updateDocumentType(@PathVariable String id, @RequestBody Map<String, Object> body) {
        int n = service.updateDocumentType(id,
                String.valueOf(body.get("codigo")),
                String.valueOf(body.get("nombre")),
                String.valueOf(body.get("flujo")));
        return n > 0 ? ResponseEntity.ok(Map.of("id", id)) : ResponseEntity.notFound().build();
    }

    @PutMapping({"/document-types/{id}/status", "/tipos-documento/{id}/estado"})
    public ResponseEntity<?> setDocumentTypeStatus(@PathVariable String id, @RequestBody Map<String, Object> body) {
        boolean active = body.get("active") == null || Boolean.parseBoolean(String.valueOf(body.get("active")));
        int n = service.setDocumentTypeStatus(id, active);
        return n > 0 ? ResponseEntity.ok(Map.of("id", id, "active", active)) : ResponseEntity.notFound().build();
    }

    @GetMapping({"/lookups/users", "/lookups/usuarios"})
    public ResponseEntity<?> lookupUsers(@RequestParam("entity_code") String entityCode,
                                         @RequestParam(required = false) String q,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.lookupUsers(entityCode, q, page, pageSize));
    }

    @GetMapping({"/lookups/persons", "/lookups/personas"})
    public ResponseEntity<?> lookupPersons(@RequestParam("entity_code") String entityCode,
                                           @RequestParam(required = false) String q,
                                           @RequestParam(defaultValue = "1") int page,
                                           @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.lookupPersons(entityCode, q, page, pageSize));
    }
}
