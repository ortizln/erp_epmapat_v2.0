package com.erp.sri_files.integration;

import com.erp.sri_files.models.DocumentoElectronico;
import com.erp.sri_files.models.Factura;
import com.erp.sri_files.repositories.DefinirR;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.repositories.FacturaR;
import com.erp.sri_files.services.DocumentoProcessManager;
import com.erp.sri_files.services.FacturaXmlGeneratorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/erp")
@Tag(name = "Integración ERP")
public class ErpIntegrationController {

    private static final Logger log = LoggerFactory.getLogger(ErpIntegrationController.class);

    @Autowired
    private DocumentoProcessManager processManager;

    @Autowired
    private DocumentoElectronicoR documentoElectronicoR;

    @Autowired
    private FacturaR facturaR;

    @Autowired
    private FacturaXmlGeneratorService facturaXmlGeneratorService;

    @Autowired
    private DefinirR definirR;

    @Value("${app.backend.base-url:http://192.168.0.165:9080}")
    private String backendBaseUrl;

    @PostMapping("/factura")
    @Operation(summary = "Procesar factura desde ERP", description = "Recibe el ID de factura, genera XML y ejecuta el flujo completo")
    @ApiResponse(responseCode = "200", description = "Factura procesada exitosamente")
    public ResponseEntity<Map<String, Object>> procesarFactura(
            @RequestBody Map<String, Object> request) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            Integer idFactura = (Integer) request.get("idfactura");
            if (idFactura == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Se requiere el campo 'idfactura'"
                ));
            }

            log.info("Recibida solicitud de factura desde ERP: idfactura={}", idFactura);

            Factura factura = facturaR.findById(idFactura.longValue()).orElse(null);
            if (factura == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Factura no encontrada con id: " + idFactura
                ));
            }

            String xmlGenerado = facturaXmlGeneratorService.generarXmlFactura(factura);

            String rucEmisor = definirR.findById(1L)
                    .map(d -> d.getRuc())
                    .orElse(null);

            DocumentoElectronico doc = new DocumentoElectronico();
            doc.setTipoDocumento("FACTURA");
            doc.setExternalId(idFactura.toString());
            doc.setRucEmisor(rucEmisor);
            doc.setIdentificacionReceptor(factura.getIdentificacioncomprador());
            doc.setRazonSocialReceptor(factura.getRazonsocialcomprador());
            doc.setEmailReceptor(factura.getEmailcomprador());
            doc.setXmlGenerado(xmlGenerado);

            var result = processManager.procesarFlujoCompleto(doc, xmlGenerado);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requestId", requestId,
                    "estado", result.estado(),
                    "documento", Map.of(
                            "uuid", doc.getUuid() != null ? doc.getUuid() : "",
                            "estado", result.estado(),
                            "mensaje", result.mensaje()
                    )
            ));
        } catch (Exception e) {
            log.error("Error procesando factura desde ERP", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } finally {
            MDC.clear();
        }
    }

    @PostMapping("/documento")
    @Operation(summary = "Procesar documento genérico desde ERP", description = "Recibe datos del documento y ejecuta flujo parcial (recibir -> validar). Para tipos no facturas el XML debe enviarse en xmlOriginal.")
    @ApiResponse(responseCode = "200", description = "Documento procesado exitosamente")
    public ResponseEntity<Map<String, Object>> procesarDocumento(
            @RequestBody Map<String, Object> request) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            String tipoDocumento = (String) request.get("tipoDocumento");
            String externalId = (String) request.get("externalId");
            String rucEmisor = (String) request.get("rucEmisor");
            String identificacionReceptor = (String) request.get("identificacionReceptor");
            String razonSocialReceptor = (String) request.get("razonSocialReceptor");
            String emailReceptor = (String) request.get("emailReceptor");
            String xmlOriginal = (String) request.get("xmlOriginal");

            if (tipoDocumento == null || externalId == null || rucEmisor == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Se requieren los campos 'tipoDocumento', 'externalId' y 'rucEmisor'"
                ));
            }

            log.info("Recibido documento desde ERP: tipo={}, externalId={}", tipoDocumento, externalId);

            DocumentoElectronico doc = new DocumentoElectronico();
            doc.setTipoDocumento(tipoDocumento);
            doc.setExternalId(externalId);
            doc.setRucEmisor(rucEmisor);
            doc.setIdentificacionReceptor(identificacionReceptor);
            doc.setRazonSocialReceptor(razonSocialReceptor);
            doc.setEmailReceptor(emailReceptor);
            doc.setXmlGenerado(xmlOriginal);

            var result = processManager.recibirDocumento(doc);
            if (!result.exitoso()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "requestId", requestId,
                        "estado", result.estado(),
                        "error", result.mensaje()
                ));
            }

            result = processManager.validar(doc);
            if (!result.exitoso()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "requestId", requestId,
                        "estado", result.estado(),
                        "error", result.mensaje()
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "requestId", requestId,
                    "estado", result.estado(),
                    "documento", Map.of(
                            "uuid", doc.getUuid() != null ? doc.getUuid() : "",
                            "estado", result.estado(),
                            "mensaje", result.mensaje()
                    )
            ));
        } catch (Exception e) {
            log.error("Error procesando documento desde ERP", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/{uuid}/estado")
    @Operation(summary = "Consultar estado de documento", description = "Consulta el estado actual de un documento por su UUID")
    @ApiResponse(responseCode = "200", description = "Estado del documento")
    public ResponseEntity<Map<String, Object>> consultarEstado(
            @Parameter(description = "UUID del documento") @PathVariable String uuid) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            log.info("Consultando estado del documento: uuid={}", uuid);

            var docOpt = documentoElectronicoR.findByUuid(uuid);
            if (docOpt.isPresent()) {
                var doc = docOpt.get();
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "requestId", requestId,
                        "uuid", uuid,
                        "tipoDocumento", doc.getTipoDocumento(),
                        "estado", doc.getEstado(),
                        "externalId", doc.getExternalId() != null ? doc.getExternalId() : ""
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Documento no encontrado con uuid: " + uuid
                ));
            }
        } catch (Exception e) {
            log.error("Error consultando estado del documento", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", e.getMessage()
            ));
        } finally {
            MDC.clear();
        }
    }

    @GetMapping("/ping")
    @Operation(summary = "Health check", description = "Verifica que el servicio de integración ERP esté activo")
    @ApiResponse(responseCode = "200", description = "Servicio activo")
    public ResponseEntity<Map<String, Object>> ping() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "sri-files-erp-integration"
        ));
    }
}
