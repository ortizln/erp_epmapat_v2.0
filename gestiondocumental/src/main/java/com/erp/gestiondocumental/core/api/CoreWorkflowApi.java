package com.erp.gestiondocumental.core.api;

import com.erp.gestiondocumental.core.service.WorkflowCoreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class CoreWorkflowApi {

    private final WorkflowCoreService service;

    public CoreWorkflowApi(WorkflowCoreService service) {
        this.service = service;
    }

    @PostMapping("/{docId}/assign")
    public ResponseEntity<?> assign(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("assignment_id", service.assign(docId, body)));
    }

    @PostMapping("/{docId}/derive")
    public ResponseEntity<?> derive(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("derivation_id", service.derive(docId, body)));
    }

    @PostMapping("/{docId}/derive/bulk")
    public ResponseEntity<?> deriveBulk(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(service.deriveBulk(docId, body));
    }

    @GetMapping("/{docId}/derivations")
    public ResponseEntity<?> derivations(@PathVariable String docId) {
        return ResponseEntity.ok(service.listDerivations(docId));
    }

    @GetMapping("/derivations/pending")
    public ResponseEntity<?> pending(@RequestParam(required = false) String to_user_id,
                                     @RequestParam(required = false) String to_dependency_id,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(name = "page_size", defaultValue = "20") int pageSize) {
        return ResponseEntity.ok(service.pendingDerivations(to_user_id, to_dependency_id, page, pageSize));
    }

    @PatchMapping("/derivations/{derivationId}/read")
    public ResponseEntity<?> markRead(@PathVariable String derivationId, @RequestBody(required = false) Map<String, Object> body) {
        String reader = body == null ? null : (String) body.get("reader_user_id");
        int n = service.markRead(derivationId, reader);
        return n > 0 ? ResponseEntity.ok(Map.of("ok", true)) : ResponseEntity.notFound().build();
    }

    @PostMapping("/{docId}/responses")
    public ResponseEntity<?> respond(@PathVariable String docId, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("response_id", service.respond(docId, body)));
    }

    @GetMapping("/{docId}/timeline")
    public ResponseEntity<?> timeline(@PathVariable String docId) {
        return ResponseEntity.ok(service.timeline(docId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<?> overdue(@RequestParam("entity_code") String entityCode) {
        return ResponseEntity.ok(service.overdue(entityCode));
    }

    @GetMapping("/due-soon")
    public ResponseEntity<?> dueSoon(@RequestParam("entity_code") String entityCode,
                                     @RequestParam(defaultValue = "48") int hours) {
        return ResponseEntity.ok(service.dueSoon(entityCode, hours));
    }
}
