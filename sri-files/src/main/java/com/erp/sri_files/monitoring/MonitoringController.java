package com.erp.sri_files.monitoring;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.erp.sri_files.batch.BatchJobR;
import com.erp.sri_files.repositories.DocumentoElectronicoR;
import com.erp.sri_files.repositories.FacturaR;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/monitoring")
@Tag(name = "Monitoring", description = "Health checks, métricas y dashboard del sistema")
public class MonitoringController {

    private final MetricsService metricsService;
    private final DocumentoElectronicoR documentoR;
    private final FacturaR facturaR;
    private final BatchJobR batchJobR;

    @Value("${spring.application.name:msvc-sri}")
    private String appName;

    @Value("${spring.profiles.active:local}")
    private String activeProfile;

    @Operation(summary = "Health check del sistema", description = "Verifica que la app y la BD estén respondiendo")
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("status", "UP");
        health.put("service", appName);
        health.put("ambiente", resolveAmbiente());
        health.put("timestamp", java.time.LocalDateTime.now().toString());

        try {
            documentoR.count();
            health.put("database", "UP");
        } catch (Exception e) {
            health.put("status", "DOWN");
            health.put("database", "DOWN");
            health.put("databaseError", e.getMessage());
        }

        return ResponseEntity.ok(health);
    }

    @Operation(summary = "Métricas del sistema", description = "Estadísticas de documentos procesados, envíos SRI, errores, etc.")
    @GetMapping("/metrics")
    public ResponseEntity<?> metrics() {
        return ResponseEntity.ok(metricsService.getMetricas());
    }

    @Operation(summary = "Dashboard de estados", description = "Conteo de documentos por estado y tipo")
    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();

        try {
            Map<String, Long> porEstado = new LinkedHashMap<>();
            for (String estado : java.util.List.of(
                    "RECIBIDO", "VALIDADO", "XML_GENERADO", "FIRMADO",
                    "RECIBIDO_SRI", "PENDIENTE_AUTORIZACION", "AUTORIZADO",
                    "NO_AUTORIZADO", "ERROR")) {
                long count = documentoR.countByEstado(estado);
                if (count > 0) porEstado.put(estado, count);
            }
            dashboard.put("documentoElectronico", porEstado);

            long facturasTotal = facturaR.count();
            dashboard.put("facturas", Map.of(
                "total", facturasTotal
            ));

            dashboard.put("timestamp", java.time.LocalDateTime.now().toString());

        } catch (Exception e) {
            dashboard.put("error", e.getMessage());
        }

        return ResponseEntity.ok(dashboard);
    }

    @Operation(summary = "Info del servicio", description = "Información básica del microservicio")
    @GetMapping("/info")
    public ResponseEntity<?> info() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("service", appName);
        info.put("version", "1.0.0");
        info.put("ambiente", resolveAmbiente());
        info.put("profile", activeProfile);
        info.put("javaVersion", System.getProperty("java.version"));
        info.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        info.put("maxMemoryMB", Runtime.getRuntime().maxMemory() / 1024 / 1024);
        info.put("totalMemoryMB", Runtime.getRuntime().totalMemory() / 1024 / 1024);
        info.put("freeMemoryMB", Runtime.getRuntime().freeMemory() / 1024 / 1024);
        return ResponseEntity.ok(info);
    }

    private String resolveAmbiente() {
        return switch (activeProfile.toLowerCase()) {
            case "prod", "produccion", "production" -> "PRODUCCIÓN";
            case "test", "staging", "qa" -> "PRUEBAS";
            default -> "DESARROLLO";
        };
    }
}
