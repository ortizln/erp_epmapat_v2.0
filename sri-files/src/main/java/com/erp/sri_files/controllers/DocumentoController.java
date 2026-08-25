package com.erp.sri_files.controllers;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri_files.models.DocumentoElectronico;
import com.erp.sri_files.monitoring.MetricsService;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.services.DocumentoProcessManager;
import com.erp.sri_files.services.XmlToPdfService;
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
    private final XmlToPdfService xmlToPdfService;
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

    @Operation(summary = "Listar comprobantes electrónicos", description = "Lista comprobantes electrónicos con paginación y filtros opcionales por tipo, estado y búsqueda",
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
            @RequestParam(required = false) String estado,
            @Parameter(description = "Búsqueda por clave acceso, UUID o número autorización")
            @RequestParam(required = false) String busqueda) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            var pageable = org.springframework.data.domain.PageRequest.of(page, size);
            var resultado = documentoR.buscar(tipo, estado, busqueda, pageable);

            var documentos = resultado.getContent().stream().map(d -> Map.<String, Object>of(
                "id", d.getId(),
                "uuid", d.getUuid(),
                "tipoDocumento", d.getTipoDocumento(),
                "estado", d.getEstado(),
                "subestado", d.getSubestado() != null ? d.getSubestado() : "",
                "claveAcceso", d.getClaveAcceso() != null ? d.getClaveAcceso() : "",
                "numeroAutorizacion", d.getNumeroAutorizacion() != null ? d.getNumeroAutorizacion() : "",
                "ambiente", d.getAmbiente() != null ? d.getAmbiente() : "",
                "fechaRecepcion", d.getFechaRecepcionJson() != null ? d.getFechaRecepcionJson().toString() : "",
                "fechaAutorizacion", d.getFechaAutorizacionSri() != null ? d.getFechaAutorizacionSri().toString() : ""
            )).toList();

            return ResponseEntity.ok(Map.of(
                "content", documentos,
                "totalElements", resultado.getTotalElements(),
                "totalPages", resultado.getTotalPages(),
                "currentPage", page,
                "size", size,
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Exportar documentos a CSV", description = "Exporta documentos filtrados a formato CSV para descarga",
        responses = {
            @ApiResponse(responseCode = "200", description = "CSV generado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @GetMapping("/export")
    public ResponseEntity<?> exportarCsv(
            @Parameter(description = "Filtrar por tipo de documento")
            @RequestParam(required = false) String tipo,
            @Parameter(description = "Filtrar por estado del documento")
            @RequestParam(required = false) String estado,
            @Parameter(description = "Búsqueda por clave acceso, UUID o número autorización")
            @RequestParam(required = false) String busqueda) {
        try {
            var pageable = org.springframework.data.domain.PageRequest.of(0, 10000);
            var resultado = documentoR.buscar(tipo, estado, busqueda, pageable);

            StringBuilder csv = new StringBuilder();
            csv.append("UUID,Tipo,Estado,ClaveAcceso,NumeroAutorizacion,Ambiente,FechaRecepcion,FechaAutorizacion\n");

            for (var d : resultado.getContent()) {
                csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    d.getUuid(),
                    d.getTipoDocumento(),
                    d.getEstado(),
                    d.getClaveAcceso() != null ? d.getClaveAcceso() : "",
                    d.getNumeroAutorizacion() != null ? d.getNumeroAutorizacion() : "",
                    d.getAmbiente() != null ? d.getAmbiente() : "",
                    d.getFechaRecepcionJson() != null ? d.getFechaRecepcionJson() : "",
                    d.getFechaAutorizacionSri() != null ? d.getFechaAutorizacionSri() : ""
                ));
            }

            byte[] csvBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"documentos.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
        } catch (Exception e) {
            log.error("Error exportando documentos", e);
            return ResponseEntity.status(500).body(Map.of("error", "Error exportando documentos"));
        }
    }

    @Operation(summary = "Descargar RIDE (PDF) de un comprobante", description = "Genera y retorna el PDF del comprobante autorizado",
        responses = {
            @ApiResponse(responseCode = "200", description = "PDF generado correctamente"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado o sin XML autorizado")
        })
    @GetMapping("/{uuid}/ride")
    public ResponseEntity<?> descargarRide(
            @Parameter(description = "UUID del documento", required = true)
            @PathVariable String uuid) {
        try {
            var docOpt = documentoR.findByUuid(uuid);
            if (docOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Documento no encontrado"));
            }
            DocumentoElectronico doc = docOpt.get();
            if (doc.getXmlAutorizado() == null || doc.getXmlAutorizado().isBlank()) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Documento sin XML autorizado",
                    "estado", doc.getEstado()
                ));
            }
            ByteArrayOutputStream pdfStream = xmlToPdfService.generarFacturaPDF_v3(doc.getXmlAutorizado());
            byte[] pdfBytes = pdfStream.toByteArray();
            String nombrePdf = doc.getTipoDocumento() + "_" + doc.getSecuencial() + ".pdf";
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombrePdf + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
        } catch (Exception e) {
            log.error("Error generando RIDE para uuid={}", uuid, e);
            return ResponseEntity.status(500).body(Map.of("error", "Error generando RIDE", "detalle", e.getMessage()));
        }
    }

    @Operation(summary = "Descargar XML autorizado", description = "Retorna el XML autorizado del comprobante",
        responses = {
            @ApiResponse(responseCode = "200", description = "XML retornado"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado o sin XML autorizado")
        })
    @GetMapping("/{uuid}/xml")
    public ResponseEntity<?> descargarXml(
            @Parameter(description = "UUID del documento", required = true)
            @PathVariable String uuid) {
        try {
            var docOpt = documentoR.findByUuid(uuid);
            if (docOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Documento no encontrado"));
            }
            DocumentoElectronico doc = docOpt.get();
            if (doc.getXmlAutorizado() == null || doc.getXmlAutorizado().isBlank()) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Documento sin XML autorizado",
                    "estado", doc.getEstado()
                ));
            }
            String nombreXml = doc.getTipoDocumento() + "_" + doc.getSecuencial() + ".xml";
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreXml + "\"")
                .contentType(MediaType.APPLICATION_XML)
                .body(doc.getXmlAutorizado().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Error descargando XML para uuid={}", uuid, e);
            return ResponseEntity.status(500).body(Map.of("error", "Error descargando XML", "detalle", e.getMessage()));
        }
    }

    @Operation(summary = "Descargar ZIP con XML + RIDE", description = "Descarga un archivo ZIP conteniendo el XML autorizado y el PDF (RIDE)",
        responses = {
            @ApiResponse(responseCode = "200", description = "ZIP generado correctamente"),
            @ApiResponse(responseCode = "404", description = "Documento no encontrado o sin XML autorizado")
        })
    @GetMapping("/{uuid}/zip")
    public ResponseEntity<?> descargarZip(
            @Parameter(description = "UUID del documento", required = true)
            @PathVariable String uuid) {
        try {
            var docOpt = documentoR.findByUuid(uuid);
            if (docOpt.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "Documento no encontrado"));
            }
            DocumentoElectronico doc = docOpt.get();
            if (doc.getXmlAutorizado() == null || doc.getXmlAutorizado().isBlank()) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Documento sin XML autorizado",
                    "estado", doc.getEstado()
                ));
            }
            ByteArrayOutputStream pdfStream = xmlToPdfService.generarFacturaPDF_v3(doc.getXmlAutorizado());
            String baseName = doc.getTipoDocumento() + "_" + doc.getSecuencial();
            ByteArrayOutputStream zipBaos = new ByteArrayOutputStream();
            try (ZipOutputStream zos = new ZipOutputStream(zipBaos)) {
                zos.putNextEntry(new ZipEntry(baseName + ".xml"));
                zos.write(doc.getXmlAutorizado().getBytes(StandardCharsets.UTF_8));
                zos.closeEntry();
                zos.putNextEntry(new ZipEntry(baseName + ".pdf"));
                zos.write(pdfStream.toByteArray());
                zos.closeEntry();
            }
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + baseName + ".zip\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(zipBaos.toByteArray());
        } catch (Exception e) {
            log.error("Error generando ZIP para uuid={}", uuid, e);
            return ResponseEntity.status(500).body(Map.of("error", "Error generando ZIP", "detalle", e.getMessage()));
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
