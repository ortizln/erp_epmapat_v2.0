package com.erp.sri_files.controllers;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri_files.models.DocumentoElectronico;
import com.erp.sri_files.monitoring.MetricsService;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.services.DocumentoProcessManager;
import com.erp.sri_files.validation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/documentos")
@Tag(name = "Documentos", description = "Endpoint genérico para los 6 tipos de comprobantes electrónicos SRI")
public class DocumentoController {

    private static final Logger log = LoggerFactory.getLogger(DocumentoController.class);

    private final DocumentoProcessManager processManager;
    private final DocumentoElectronicoR documentoR;
    private final MetricsService metricsService;
    private final SriLiquidacionCompraValidationService liquidacionValidationService;
    private final SriGuiaRemisionValidationService guiaRemisionValidationService;
    private final com.erp.sri_files.validation.SriRetencionValidationService retencionValidationService;
    private final com.erp.sri_files.validation.SriNotaCreditoValidationService notaCreditoValidationService;
    private final com.erp.sri_files.validation.SriNotaDebitoValidationService notaDebitoValidationService;

    @Operation(summary = "Recibir y procesar comprobante electrónico", description = """
            Endpoint genérico que detecta el tipo de documento desde la raíz XML y ejecuta el flujo completo:
            recepción → validación → firma → envío SRI → autorización.
            
            Tipos soportados: factura, retención, notaCredito, notaDebito, liquidacionCompra, guiaRemision
            
            Retorna el estado final (AUTORIZADO o PENDIENTE_AUTORIZACION) con el requestId para tracking.
            """,
        responses = {
            @ApiResponse(responseCode = "200", description = "Comprobante autorizado por SRI"),
            @ApiResponse(responseCode = "202", description = "Comprobante pendiente de autorización"),
            @ApiResponse(responseCode = "400", description = "Validación previa fallida o SRI rechazó"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @PostMapping(consumes = MediaType.APPLICATION_XML_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> recibirDocumento(
            @Parameter(description = "XML del comprobante electrónico (factura, retención, etc.)", required = true,
                example = "<factura id=\"comprobante\" version=\"1.0.0\">...</factura>")
            @RequestBody String xmlBody) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        String tipoDocumento = "UNKNOWN";

        try {
            if (xmlBody == null || xmlBody.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "XML vacío o nulo",
                    "requestId", requestId
                ));
            }

            String xmlPlano = SriControllerHelper.stripBom(xmlBody).trim();
            tipoDocumento = detectarTipoDocumento(xmlPlano);
            
            if (tipoDocumento == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se pudo detectar el tipo de documento. Raíz XML no reconocida",
                    "requestId", requestId
                ));
            }

            MDC.put("tipoDocumento", tipoDocumento);
            log.info("Documento detectado: {} [requestId={}]", tipoDocumento, requestId);

            var validacion = validarDocumento(tipoDocumento, xmlPlano);
            if (!validacion.valid()) {
                log.warn("Validación previa fallida para {}: {} [requestId={}]", 
                    tipoDocumento, validacion.errors(), requestId);
                return ResponseEntity.badRequest().body(Map.of(
                    "estado", "VALIDACION_PREVIA_FALLIDA",
                    "tipoDocumento", tipoDocumento,
                    "requestId", requestId,
                    "errores", validacion.errors(),
                    "warnings", validacion.warnings()
                ));
            }

            DocumentoElectronico doc = new DocumentoElectronico();
            doc.setUuid(UUID.randomUUID().toString());
            doc.setTipoDocumento(tipoDocumento);
            doc.setClaveAcceso(validacion.claveAcceso());
            doc.setAmbiente(validacion.ambiente() != null ? Integer.parseInt(validacion.ambiente()) : null);
            doc.setXmlGenerado(xmlPlano);
            doc.setRucEmisor(ExtraerCampo.extraer(xmlPlano, "ruc"));
            doc.setEstablecimiento(ExtraerCampo.extraer(xmlPlano, "estab"));
            doc.setPuntoEmision(ExtraerCampo.extraer(xmlPlano, "ptoEmi"));
            doc.setSecuencial(ExtraerCampo.extraer(xmlPlano, "secuencial"));

            var result = processManager.recibirDocumento(doc);
            if (!result.exitoso()) {
                return ResponseEntity.status(500).body(Map.of(
                    "error", result.mensaje(),
                    "tipoDocumento", tipoDocumento,
                    "requestId", requestId
                ));
            }

            result = processManager.validar(doc);
            if (!result.exitoso()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "estado", result.estado(),
                    "tipoDocumento", tipoDocumento,
                    "requestId", requestId,
                    "error", result.mensaje()
                ));
            }

            result = processManager.generarXml(doc, xmlPlano);
            if (!result.exitoso()) {
                return ResponseEntity.status(500).body(Map.of(
                    "estado", result.estado(),
                    "tipoDocumento", tipoDocumento,
                    "requestId", requestId,
                    "error", result.mensaje()
                ));
            }

            result = processManager.firmar(doc, xmlPlano);
            if (!result.exitoso()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "estado", result.estado(),
                    "tipoDocumento", tipoDocumento,
                    "requestId", requestId,
                    "error", result.mensaje()
                ));
            }

            result = processManager.enviarASri(doc);
            if (!result.exitoso()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "estado", result.estado(),
                    "tipoDocumento", tipoDocumento,
                    "requestId", requestId,
                    "error", result.mensaje()
                ));
            }

            result = processManager.consultarAutorizacion(doc);
            if (!result.exitoso()) {
                return ResponseEntity.status(202).body(Map.of(
                    "estado", result.estado(),
                    "tipoDocumento", tipoDocumento,
                    "requestId", requestId
                ));
            }

            result = processManager.finalizar(doc);

            metricsService.incrementarRecibidos(tipoDocumento);
            metricsService.incrementarAutorizados(tipoDocumento);

            log.info("Proceso completado: {} clave={} [requestId={}]", 
                tipoDocumento, validacion.claveAcceso(), requestId);

            return ResponseEntity.ok(Map.of(
                "estado", "AUTORIZADO",
                "tipoDocumento", tipoDocumento,
                "numeroAutorizacion", doc.getNumeroAutorizacion() != null ? doc.getNumeroAutorizacion() : "",
                "claveAcceso", validacion.claveAcceso() != null ? validacion.claveAcceso() : "",
                "requestId", requestId
            ));

        } catch (Exception e) {
            metricsService.incrementarFallidos(tipoDocumento != null ? tipoDocumento : "UNKNOWN");
            log.error("Error procesando documento [requestId={}]", requestId, e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error interno procesando documento",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Consultar estado de un comprobante por UUID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Documento encontrado"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado")
        })
    @GetMapping("/{uuid}")
    public ResponseEntity<?> consultar(
            @Parameter(description = "UUID del documento", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String uuid) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            return documentoR.findByUuid(uuid)
                .map(doc -> ResponseEntity.ok((Object) Map.of(
                    "uuid", doc.getUuid(),
                    "tipoDocumento", doc.getTipoDocumento(),
                    "estado", doc.getEstado(),
                    "subestado", doc.getSubestado() != null ? doc.getSubestado() : "",
                    "claveAcceso", doc.getClaveAcceso() != null ? doc.getClaveAcceso() : "",
                    "numeroAutorizacion", doc.getNumeroAutorizacion() != null ? doc.getNumeroAutorizacion() : "",
                    "ambiente", doc.getAmbiente() != null ? doc.getAmbiente() : "",
                    "fechaRecepcion", doc.getFechaRecepcionJson() != null ? doc.getFechaRecepcionJson().toString() : "",
                    "fechaAutorizacion", doc.getFechaAutorizacionSri() != null ? doc.getFechaAutorizacionSri().toString() : "",
                    "requestId", requestId
                )))
                .orElse(ResponseEntity.notFound().build());

        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Listar comprobantes electrónicos", description = "Lista comprobantes electrónicos con paginación y filtros opcionales por tipo y estado",
        responses = {
            @ApiResponse(responseCode = "200", description = "Listado de comprobantes"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @GetMapping
    public ResponseEntity<?> listar(
            @Parameter(description = "Número de página (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de la página", example = "20")
            @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filtrar por tipo de documento", example = "FACTURA")
            @RequestParam(required = false) String tipo,
            @Parameter(description = "Filtrar por estado del documento", example = "AUTORIZADO")
            @RequestParam(required = false) String estado) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            return ResponseEntity.ok(Map.of(
                "page", page,
                "size", size,
                "requestId", requestId,
                "mensaje", "Endpoint de listado pendiente de implementación completa"
            ));
        } finally {
            MDC.clear();
        }
    }

    private String detectarTipoDocumento(String xml) {
        if (xml.contains("<factura"))          return "FACTURA";
        if (xml.contains("<retencion"))        return "RETENCION";
        if (xml.contains("<notaCredito"))      return "NOTA_CREDITO";
        if (xml.contains("<notaDebito"))       return "NOTA_DEBITO";
        if (xml.contains("<liquidacionCompra")) return "LIQUIDACION_COMPRA";
        if (xml.contains("<guiaRemision"))     return "GUIA_REMISION";
        return null;
    }

    private ValidacionGenerico validarDocumento(String tipoDocumento, String xml) {
        return switch (tipoDocumento) {
            case "LIQUIDACION_COMPRA" -> {
                var r = liquidacionValidationService.validate(xml);
                yield new ValidacionGenerico(r.valid(), r.errors(), r.warnings(), r.claveAcceso(), r.ambiente(), r.codDoc());
            }
            case "GUIA_REMISION" -> {
                var r = guiaRemisionValidationService.validate(xml);
                yield new ValidacionGenerico(r.valid(), r.errors(), r.warnings(), r.claveAcceso(), r.ambiente(), r.codDoc());
            }
            case "RETENCION" -> {
                var r = retencionValidationService.validate(xml);
                yield new ValidacionGenerico(r.valid(), r.errors(), r.warnings(), r.claveAcceso(), r.ambiente(), r.codDoc());
            }
            case "NOTA_CREDITO" -> {
                var r = notaCreditoValidationService.validate(xml);
                yield new ValidacionGenerico(r.valid(), r.errors(), r.warnings(), r.claveAcceso(), r.ambiente(), r.codDoc());
            }
            case "NOTA_DEBITO" -> {
                var r = notaDebitoValidationService.validate(xml);
                yield new ValidacionGenerico(r.valid(), r.errors(), r.warnings(), r.claveAcceso(), r.ambiente(), r.codDoc());
            }
            case "FACTURA" -> new ValidacionGenerico(true, java.util.List.of(), java.util.List.of(), ExtraerCampo.extraer(xml, "claveAcceso"), ExtraerCampo.extraer(xml, "ambiente"), ExtraerCampo.extraer(xml, "codDoc"));
            default -> new ValidacionGenerico(false, java.util.List.of("Tipo no soportado"), java.util.List.of(), null, null, null);
        };
    }

    private record ValidacionGenerico(boolean valid, java.util.List<String> errors, java.util.List<String> warnings, String claveAcceso, String ambiente, String codDoc) {}

    private static class ExtraerCampo {
        static String extraer(String xml, String tag) {
            int start = xml.indexOf("<" + tag + ">");
            if (start == -1) return null;
            start += ("<" + tag + ">").length();
            int end = xml.indexOf("</" + tag + ">", start);
            if (end == -1) return null;
            String val = xml.substring(start, end).trim();
            return val.isEmpty() ? null : val;
        }
    }
}
