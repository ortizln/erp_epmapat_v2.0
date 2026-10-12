package com.erp.sri_files.controllers;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.SendXmlToSriService;

import ec.gob.sri.ws.autorizacion.RespuestaComprobante;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/autorizacion")
@Tag(name = "Autorización", description = "Consulta de autorizaciones SRI")
public class AutorizacionController {

    private static final Logger log = LoggerFactory.getLogger(AutorizacionController.class);

    private final SendXmlToSriService sendXmlToSriService;
    private final FacturaR facturaR;
    private final com.erp.sri_files.services.XmlAutorizadoRecoveryService xmlRecovery;

    @Operation(summary = "Consultar autorización SRI por clave de acceso", description = "Consulta el estado de autorización de un comprobante en el SRI utilizando su clave de acceso",
        responses = {
            @ApiResponse(responseCode = "200", description = "Estado de autorización consultado"),
            @ApiResponse(responseCode = "500", description = "Error consultando autorización en SRI")
        })
    @GetMapping("/{claveAcceso}")
    public ResponseEntity<?> consultarPorClave(
            @Parameter(description = "Clave de acceso del comprobante", required = true, example = "01012024011792148581001100100100001234567890001")
            @PathVariable String claveAcceso,
            @Parameter(description = "Esperar a que el SRI autorice el comprobante", example = "false")
            @RequestParam(defaultValue = "false") boolean wait,
            @Parameter(description = "Número de intentos de consulta cuando se espera", example = "10")
            @RequestParam(defaultValue = "10") int attempts,
            @Parameter(description = "Tiempo de espera entre intentos en milisegundos", example = "3000")
            @RequestParam(defaultValue = "3000") long sleepMillis,
            @Parameter(description = "Incluir el XML autorizado en la respuesta", example = "false")
            @RequestParam(defaultValue = "false") boolean includeXml
    ) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("claveAcceso", claveAcceso);

        try {
            RespuestaComprobante rc;
            if (wait) {
                rc = sendXmlToSriService.consultarAutorizacionConEspera(
                    null,
                    clave -> {
                        try { return sendXmlToSriService.consultarAutorizacion(claveAcceso); }
                        catch (Exception e) { throw new RuntimeException(e); }
                    },
                    attempts,
                    sleepMillis
                );
            } else {
                rc = sendXmlToSriService.consultarAutorizacion(claveAcceso);
            }
            return ResponseEntity.ok(SriControllerHelper.mapRespuestaAutorizacion(rc, includeXml));
        } catch (Exception e) {
            log.error("Error consultando autorizacion", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error consultando autorizacion en SRI",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Consultar autorización SRI desde XML", description = "Extrae la clave de acceso del XML y consulta su estado de autorización en el SRI",
        responses = {
            @ApiResponse(responseCode = "200", description = "Estado de autorización consultado"),
            @ApiResponse(responseCode = "400", description = "XML vacío o sin clave de acceso"),
            @ApiResponse(responseCode = "500", description = "Error consultando autorización en SRI")
        })
    @PostMapping(
        path = "/by-xml",
        consumes = MediaType.APPLICATION_XML_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> consultarDesdeXml(
            @Parameter(description = "XML del comprobante electrónico", required = true)
            @RequestBody String xml,
            @Parameter(description = "Esperar a que el SRI autorice el comprobante", example = "false")
            @RequestParam(defaultValue = "false") boolean wait,
            @Parameter(description = "Número de intentos de consulta cuando se espera", example = "10")
            @RequestParam(defaultValue = "10") int attempts,
            @Parameter(description = "Tiempo de espera entre intentos en milisegundos", example = "3000")
            @RequestParam(defaultValue = "3000") long sleepMillis,
            @Parameter(description = "Incluir el XML autorizado en la respuesta", example = "false")
            @RequestParam(defaultValue = "false") boolean includeXml
    ) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            if (xml == null || xml.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "XML vacio",
                    "requestId", requestId
                ));
            }
            String clave = SriControllerHelper.extraerClaveAcceso(xml);
            if (clave == null || clave.isBlank()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "No se encontro <claveAcceso> en el XML",
                    "requestId", requestId
                ));
            }

            MDC.put("claveAcceso", clave);

            RespuestaComprobante rc;
            if (wait) {
                rc = sendXmlToSriService.consultarAutorizacionConEspera(
                    xml,
                    k -> {
                        try { return sendXmlToSriService.consultarAutorizacion(clave); }
                        catch (Exception e) { throw new RuntimeException(e); }
                    },
                    attempts,
                    sleepMillis
                );
            } else {
                rc = sendXmlToSriService.consultarAutorizacion(clave);
            }
            return ResponseEntity.ok(SriControllerHelper.mapRespuestaAutorizacion(rc, includeXml));
        } catch (Exception e) {
            log.error("Error consultando autorizacion desde XML", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error consultando autorizacion en SRI",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Obtener XML autorizado", description = "Retorna el XML autorizado. Primero busca en la BD; si no existe, consulta el SRI",
        responses = {
            @ApiResponse(responseCode = "200", description = "XML autorizado retornado"),
            @ApiResponse(responseCode = "404", description = "No se encontró autorización"),
            @ApiResponse(responseCode = "500", description = "Error al obtener XML autorizado")
        })
    @GetMapping("/{claveAcceso}/xml")
    public ResponseEntity<?> obtenerXmlAutorizado(
            @Parameter(description = "Clave de acceso del comprobante", required = true)
            @PathVariable String claveAcceso,
            @Parameter(description = "Forzar consulta al SRI aunque exista en BD", example = "false")
            @RequestParam(defaultValue = "false") boolean forzarSri
    ) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("claveAcceso", claveAcceso);

        try {
            var resultado = xmlRecovery.recuperar(claveAcceso, forzarSri);
            if (!resultado.recuperado()) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", resultado.mensaje() != null ? resultado.mensaje() : "Sin XML autorizado disponible",
                    "claveAcceso", claveAcceso, "requestId", requestId
                ));
            }
            boolean actualizado = guardarXmlAutorizadoEnFactura(claveAcceso.trim(), resultado.xml());
            return ResponseEntity.ok(Map.of(
                "fuente", resultado.fuente(), "claveAcceso", claveAcceso.trim(),
                "estado", resultado.estado() != null ? resultado.estado() : "",
                "numeroAutorizacion", resultado.numeroAutorizacion() != null ? resultado.numeroAutorizacion() : "",
                "actualizadoEnFecFactura", actualizado, "xmlAutorizado", resultado.xml()
            ));
        } catch (Exception e) {
            log.error("Error obteniendo XML autorizado", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error obteniendo XML autorizado",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    private boolean guardarXmlAutorizadoEnFactura(String claveAcceso, String xmlAutorizado) {
        int actualizadas = facturaR.actualizarXmlAutorizadoPorClave(claveAcceso, xmlAutorizado);
        log.info("XML autorizado actualizado en fec_factura filas={}", actualizadas);
        return actualizadas > 0;
    }
}
