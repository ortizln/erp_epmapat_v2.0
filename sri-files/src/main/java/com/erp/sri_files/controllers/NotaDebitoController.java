package com.erp.sri_files.controllers;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri_files.services.SriGateway;
import com.erp.sri_files.services.SignService;
import com.erp.sri_files.validation.SriNotaDebitoValidationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notas-debito")
@Tag(name = "Notas de Débito", description = "Gestión de notas de débito electrónicas")
public class NotaDebitoController {

    private static final Logger log = LoggerFactory.getLogger(NotaDebitoController.class);

    private final SriNotaDebitoValidationService validationService;
    private final SignService signService;
    private final SriGateway sriGateway;

    @Operation(summary = "Crear y procesar nota de débito electrónica", description = "Recibe un XML de nota de débito, lo valida, firma y envía al SRI para autorización",
        responses = {
            @ApiResponse(responseCode = "200", description = "Nota de débito autorizada por SRI"),
            @ApiResponse(responseCode = "202", description = "Nota de débito pendiente de autorización"),
            @ApiResponse(responseCode = "400", description = "Validación previa fallida o SRI rechazó"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crear(
            @Parameter(description = "XML de la nota de débito electrónica", required = true)
            @RequestBody String xmlBody) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("tipoDocumento", "NOTA_DEBITO");

        try {
            if (xmlBody == null || xmlBody.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "XML vacío o nulo",
                    "requestId", requestId
                ));
            }

            String xmlPlano = SriControllerHelper.stripBom(xmlBody).trim();
            
            log.info("Recibida nota de débito para procesar [requestId={}]", requestId);

            var validation = validationService.validate(xmlPlano);
            if (!validation.valid()) {
                log.warn("Nota de débito rechazada por validación previa: {} [requestId={}]", 
                    validation.errors(), requestId);
                return ResponseEntity.badRequest().body(Map.of(
                    "estado", "VALIDACION_PREVIA_FALLIDA",
                    "requestId", requestId,
                    "errores", validation.errors(),
                    "warnings", validation.warnings()
                ));
            }

            var firmaResult = signService.firmar(xmlPlano);
            if (!firmaResult.exitoso()) {
                log.error("Error firmando nota de débito: {} [requestId={}]", firmaResult.mensaje(), requestId);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error firmando comprobante",
                    "detalle", firmaResult.mensaje(),
                    "requestId", requestId
                ));
            }

            var recepcionResult = sriGateway.enviarRecepcion(firmaResult.xmlFirmado());
            
            if (recepcionResult.resultado() != SriGateway.ResultadoRecepcion.RECIBIDA) {
                log.warn("Nota de débito no recibida por SRI: {} [requestId={}]", 
                    recepcionResult.mensaje(), requestId);
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "SRI no recibió el comprobante",
                    "detalle", recepcionResult.mensaje(),
                    "requestId", requestId
                ));
            }

            var authResult = sriGateway.consultarAutorizacionConPolling(firmaResult.xmlFirmado(), 10, 4000);
            
            if (authResult.autorizado()) {
                log.info("Nota de débito autorizada: nro={} [requestId={}]", 
                    authResult.numeroAutorizacion(), requestId);
                
                return ResponseEntity.ok(Map.of(
                    "estado", "AUTORIZADO",
                    "numeroAutorizacion", authResult.numeroAutorizacion() != null ? authResult.numeroAutorizacion() : "",
                    "claveAcceso", validation.claveAcceso() != null ? validation.claveAcceso() : "",
                    "requestId", requestId
                ));
            }

            return ResponseEntity.status(202).body(Map.of(
                "estado", "PENDIENTE_AUTORIZACION",
                "requestId", requestId
            ));

        } catch (Exception e) {
            log.error("Error procesando nota de débito [requestId={}]", requestId, e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error interno procesando nota de débito",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Validar XML de nota de débito", description = "Valida un XML de nota de débito contra las reglas del SRI antes de enviarlo",
        responses = {
            @ApiResponse(responseCode = "200", description = "Resultado de la validación"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @PostMapping(path = "/validar", consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> validar(
            @Parameter(description = "XML de la nota de débito a validar", required = true)
            @RequestBody String xmlBody) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            if (xmlBody == null || xmlBody.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "XML vacío o nulo",
                    "requestId", requestId
                ));
            }

            String xmlPlano = SriControllerHelper.stripBom(xmlBody).trim();
            var validation = validationService.validate(xmlPlano);

            return ResponseEntity.ok(Map.of(
                "valid", validation.valid(),
                "errors", validation.errors(),
                "warnings", validation.warnings(),
                "claveAcceso", validation.claveAcceso() != null ? validation.claveAcceso() : "",
                "ambiente", validation.ambiente() != null ? validation.ambiente() : "",
                "requestId", requestId
            ));

        } catch (Exception e) {
            log.error("Error validando nota de débito [requestId={}]", requestId, e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error interno validando nota de débito",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Consultar autorización SRI", description = "Consulta el estado de autorización de una nota de débito en el SRI",
        responses = {
            @ApiResponse(responseCode = "200", description = "Estado de autorización consultado"),
            @ApiResponse(responseCode = "500", description = "Error consultando autorización en SRI")
        })
    @GetMapping("/{claveAcceso}/autorizacion")
    public ResponseEntity<?> consultarAutorizacion(
            @Parameter(description = "Clave de acceso del comprobante", required = true, example = "01012024011792148581001100100100001234567890001")
            @PathVariable String claveAcceso) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("claveAcceso", claveAcceso);

        try {
            var authResult = sriGateway.consultarAutorizacion(claveAcceso);
            
            return ResponseEntity.ok(Map.of(
                "autorizado", authResult.autorizado(),
                "mensaje", authResult.mensaje() != null ? authResult.mensaje() : "",
                "numeroAutorizacion", authResult.numeroAutorizacion() != null ? authResult.numeroAutorizacion() : "",
                "requestId", requestId
            ));

        } catch (Exception e) {
            log.error("Error consultando autorización nota de débito [requestId={}]", requestId, e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error consultando autorización",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }
}
