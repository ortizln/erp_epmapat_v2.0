package com.erp.sri_files.controllers;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/facturas")
@Tag(name = "Facturas", description = "Gestión de facturas electrónicas")
public class FacturaController {

    private static final Logger log = LoggerFactory.getLogger(FacturaController.class);

    private final FacturaR fecFacturaR;
    private final FacturaXmlGeneratorService facturaXmlGeneratorService;
    private final FacturaHistorialService historialService;
    private final SriGateway sriGateway;
    private final SignService signService;

    @Value("${app.backend.base-url:http://192.168.0.165:9080}")
    private String backendBaseUrl;

    @Operation(summary = "Crear y procesar factura electrónica", description = "Recibe el ID de una factura, genera el XML, lo firma y lo envía al SRI para autorización",
        responses = {
            @ApiResponse(responseCode = "200", description = "Factura autorizada por SRI"),
            @ApiResponse(responseCode = "202", description = "Factura pendiente de autorización"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida o factura no encontrada"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada en base de datos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @PostMapping
    public ResponseEntity<?> crear(
            @Parameter(description = "Cuerpo de la solicitud con el ID de la factura", required = true)
            @RequestBody Map<String, Object> body) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("tipoDocumento", "FACTURA");
        
        try {
            Long idfactura = body.get("idfactura") != null 
                ? Long.valueOf(body.get("idfactura").toString()) 
                : null;
            
            if (idfactura == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Se requiere idfactura",
                    "requestId", requestId
                ));
            }

            var factura = fecFacturaR.findByIdfactura(idfactura);
            if (factura == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Factura no encontrada: " + idfactura,
                    "requestId", requestId
                ));
            }

            MDC.put("idFactura", String.valueOf(idfactura));
            log.info("Procesando factura {} via API V1", idfactura);

            historialService.registrarCambioEstado(factura, factura.getEstado(), "PROCESANDO", "Inicio procesamiento API V1");

            String xmlPlano = facturaXmlGeneratorService.generarXmlFactura(factura);
            
            var firmaResult = signService.firmar(xmlPlano);
            if (!firmaResult.exitoso()) {
                historialService.registrarError(factura, "FIRMA", firmaResult.mensaje());
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Error firmando comprobante",
                    "detalle", firmaResult.mensaje(),
                    "requestId", requestId
                ));
            }
            
            var recepcionResult = sriGateway.enviarRecepcion(firmaResult.xmlFirmado());
            
            if (recepcionResult.resultado() != SriGateway.ResultadoRecepcion.RECIBIDA) {
                historialService.registrarError(factura, "RECEPCION_SRI", "No recibida: " + recepcionResult.mensaje());
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "SRI no recibio el comprobante",
                    "detalle", recepcionResult.mensaje(),
                    "requestId", requestId
                ));
            }

            historialService.registrarCambioEstado(factura, "PROCESANDO", "ENVIADO_SRI", "Enviada a SRI correctamente");

            var authResult = sriGateway.consultarAutorizacionConPolling(firmaResult.xmlFirmado(), 10, 4000);
            
            if (authResult.autorizado()) {
                factura.setXmlautorizado(authResult.xmlAutorizado());
                factura.setEstado("A");
                fecFacturaR.save(factura);
                
                historialService.registrarCambioEstado(factura, "ENVIADO_SRI", "AUTORIZADO", "Autorizada por SRI");

                return ResponseEntity.ok(Map.of(
                    "idfactura", idfactura,
                    "estado", "AUTORIZADO",
                    "numeroAutorizacion", authResult.numeroAutorizacion() != null ? authResult.numeroAutorizacion() : "",
                    "requestId", requestId
                ));
            }

            historialService.registrarCambioEstado(factura, "ENVIADO_SRI", "PENDIENTE_AUTORIZACION", "Esperando autorizacion SRI");
            return ResponseEntity.status(202).body(Map.of(
                "estado", "PENDIENTE_AUTORIZACION",
                "requestId", requestId
            ));

        } catch (Exception e) {
            log.error("Error procesando factura via V1", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error interno",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Consultar factura por ID", description = "Obtiene los datos de una factura electrónica por su ID interno",
        responses = {
            @ApiResponse(responseCode = "200", description = "Factura encontrada"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
        })
    @GetMapping("/{idfactura}")
    public ResponseEntity<?> consultar(
            @Parameter(description = "ID de la factura", required = true, example = "12345")
            @PathVariable Long idfactura) {
        try {
            var factura = fecFacturaR.findByIdfactura(idfactura);
            if (factura == null) {
                return ResponseEntity.status(404).body(Map.of(
                    "error", "Factura no encontrada"
                ));
            }
            return ResponseEntity.ok(Map.of(
                "idfactura", factura.getIdfactura(),
                "claveAcceso", SriControllerHelper.safeStr(factura.getClaveacceso()),
                "estado", SriControllerHelper.safeStr(factura.getEstado()),
                "secuencial", SriControllerHelper.safeStr(factura.getSecuencial()),
                "identificacionComprador", SriControllerHelper.safeStr(factura.getIdentificacioncomprador()),
                "emailComprador", SriControllerHelper.safeStr(factura.getEmailcomprador()),
                "tieneXmlAutorizado", factura.getXmlautorizado() != null,
                "tieneErrores", factura.getErrores() != null && !factura.getErrores().isBlank()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
