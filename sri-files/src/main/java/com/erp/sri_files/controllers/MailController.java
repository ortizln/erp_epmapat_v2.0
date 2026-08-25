package com.erp.sri_files.controllers;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri_files.dto.AttachmentDTO;
import com.erp.sri_files.dto.SendMailRequest;
import com.erp.sri_files.dto.SendMailResponse;
import com.erp.sri_files.dto.TemplateMailRequest;
import com.erp.sri_files.services.MailService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/correo")
@Tag(name = "Correo", description = "Envío de correos electrónicos")
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);

    private final MailService mailService;

    @Operation(summary = "Enviar correo electrónico", description = "Envía un correo electrónico con el XML del comprobante adjunto",
        responses = {
            @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error enviando el correo")
        })
    @PostMapping("/send")
    public ResponseEntity<SendMailResponse> send(
            @Parameter(description = "Solicitud de envío de correo con destininatario, asunto y contenido", required = true)
            @Valid @RequestBody SendMailRequest req) {
        try {
            mailService.send(req);
            return ResponseEntity.ok(new SendMailResponse(true, "Enviado", Instant.now()));
        } catch (Exception e) {
            log.error("Error enviando correo", e);
            return ResponseEntity.status(500).body(new SendMailResponse(false, e.getMessage(), Instant.now()));
        }
    }

    @Operation(summary = "Enviar correo con plantilla", description = "Envía un correo electrónico utilizando una plantilla predefinida",
        responses = {
            @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error enviando el correo")
        })
    @PostMapping("/send-template")
    public ResponseEntity<SendMailResponse> sendTemplate(
            @Parameter(description = "Solicitud de envío de correo con plantilla", required = true)
            @Valid @RequestBody TemplateMailRequest req) {
        try {
            mailService.sendTemplate(req);
            return ResponseEntity.ok(new SendMailResponse(true, "Enviado", Instant.now()));
        } catch (Exception e) {
            log.error("Error enviando correo con plantilla", e);
            return ResponseEntity.status(500).body(new SendMailResponse(false, e.getMessage(), Instant.now()));
        }
    }

    @Operation(summary = "Verificar estado del servicio SMTP", description = "Consulta el estado de conectividad del servidor de correo SMTP",
        responses = {
            @ApiResponse(responseCode = "200", description = "Estado del SMTP consultado")
        })
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        boolean ok = mailService.smtpHealth();
        return ResponseEntity.ok(Map.of(
            "smtp", ok ? "UP" : "DOWN",
            "time", Instant.now()
        ));
    }

    @Operation(summary = "Enviar correo de prueba", description = "Envía un correo de prueba a la dirección indicada para verificar que el servicio de correo funciona correctamente",
        responses = {
            @ApiResponse(responseCode = "200", description = "Correo de prueba enviado"),
            @ApiResponse(responseCode = "500", description = "Error enviando correo de prueba")
        })
    @PostMapping("/test")
    public ResponseEntity<?> testMail(
            @Parameter(description = "Correo destino para la prueba", required = true, example = "admin@epmapatulcan.gob.ec")
            @RequestBody String destino) {
        try {
            SendMailRequest req = new SendMailRequest(
                "facturacion@epmapatulcan.gob.ec",
                java.util.List.of(destino.trim()),
                null,
                null,
                "Prueba de correo - EPMAPA-T",
                "<h2>Correo de prueba</h2><p>Si recibes este mensaje, el servicio de correo funciona correctamente.</p><p><b>Fecha:</b> " + java.time.LocalDateTime.now() + "</p>",
                null,
                null
            );
            mailService.send(req);
            log.info("Correo de prueba enviado a {}", destino);
            return ResponseEntity.ok(Map.of(
                "exito", true,
                "mensaje", "Correo de prueba enviado a " + destino,
                "timestamp", Instant.now()
            ));
        } catch (Exception e) {
            log.error("Error enviando correo de prueba", e);
            return ResponseEntity.status(500).body(Map.of(
                "exito", false,
                "error", e.getMessage(),
                "timestamp", Instant.now()
            ));
        }
    }
}
