package com.erp.emails.controller;

import com.erp.emails.component.EmailSpecs;
import com.erp.emails.dtos.EmailResponse;
import com.erp.emails.dtos.SendEmailRequest;
import com.erp.emails.model.EmailMessage;
import com.erp.emails.model.EmailStatus;
import com.erp.emails.model.EmailType;
import com.erp.emails.repository.EmailMessageR;
import com.erp.emails.service.EmailComposerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/emails")
public class EmailController {

    private final EmailComposerService composer;
    private final EmailMessageR emailRepo;

    public EmailController(EmailComposerService composer, EmailMessageR emailRepo) {
        this.composer = composer;
        this.emailRepo = emailRepo;
    }

    // 1) Documentos (XML/PDF)
    @PostMapping("/documents")
    public ResponseEntity<?> sendDocuments(@Valid @RequestBody SendEmailRequest req) {
        UUID id = composer.enqueue(EmailType.DOC_ELECTRONICO, req);
        return ResponseEntity.accepted().body(new IdResponse(id));
    }

    // 2) Notificaciones
    @PostMapping("/notifications")
    public ResponseEntity<?> sendNotifications(@Valid @RequestBody SendEmailRequest req) {
        UUID id = composer.enqueue(EmailType.NOTIFICACION, req);
        return ResponseEntity.accepted().body(new IdResponse(id));
    }

    // 3) Custom
    @PostMapping("/custom")
    public ResponseEntity<?> sendCustom(@Valid @RequestBody SendEmailRequest req) {
        UUID id = composer.enqueue(EmailType.CUSTOM, req);
        return ResponseEntity.accepted().body(new IdResponse(id));
    }

    // Admin: detalle
    @GetMapping("/{id}")
    public ResponseEntity<EmailResponse> getOne(@PathVariable UUID id) {
        return emailRepo.findById(id)
                .map(e -> ResponseEntity.ok(toResponse(e)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Admin: listar simple (puedes mejorar con Specifications)
    @GetMapping
    public Page<EmailResponse> list(
            @RequestParam(required = false) EmailStatus status,
            @RequestParam(required = false) EmailType type,
            @RequestParam(required = false) String correlationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        var spec = EmailSpecs.filter(status, type, correlationId);
        return emailRepo.findAll(spec, pageable).map(this::toResponse);
    }

    // Admin: retry (si está FAILED o PENDING con intentos < max)
    @PostMapping("/{id}/retry")
    public ResponseEntity<?> retry(@PathVariable UUID id) {
        return emailRepo.findById(id).map(e -> {
            e.setStatus(EmailStatus.PENDING);
            e.setLastError(null);
            emailRepo.save(e);
            return ResponseEntity.accepted().body(new IdResponse(id));
        }).orElse(ResponseEntity.notFound().build());
    }

    // Admin: cancel (solo si PENDING)
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancel(@PathVariable UUID id) {
        return emailRepo.findById(id).map(e -> {
            if (e.getStatus() == EmailStatus.PENDING) {
                e.setStatus(EmailStatus.CANCELLED);
                emailRepo.save(e);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.status(409).body("Solo se puede cancelar si está PENDING");
        }).orElse(ResponseEntity.notFound().build());
    }

    private EmailResponse toResponse(EmailMessage e) {
        EmailResponse r = new EmailResponse();
        r.id = e.getId();
        r.type = e.getType();
        r.status = e.getStatus();
        r.subject = e.getSubject();
        r.correlationId = e.getCorrelationId();
        r.attempts = e.getAttempts();
        r.lastError = e.getLastError();
        r.createdAt = e.getCreatedAt();
        r.sentAt = e.getSentAt();
        return r;
    }

    public record IdResponse(UUID id) {}
}