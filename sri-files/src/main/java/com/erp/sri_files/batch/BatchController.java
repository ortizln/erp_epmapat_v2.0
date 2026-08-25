package com.erp.sri_files.batch;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batch")
@Tag(name = "Batch Processor", description = "Procesamiento masivo de comprobantes electrónicos")
public class BatchController {

    private static final Logger log = LoggerFactory.getLogger(BatchController.class);

    private final BatchProcessorService batchProcessorService;
    private final BatchJobR batchJobR;

    @Operation(summary = "Ejecutar batch de procesamiento", description = """
            Ejecuta un proceso batch sobre documentos pendientes.
            
            Tipos disponibles:
            - ENVIAR_SRI: Envía documentos firmados al SRI
            - CONSULTAR_AUTORIZACION: Consulta estado de autorización
            - REINTENTAR_FALLIDOS: Reintenta documentos con error
            - ENVIAR_CORREO: Envía correos con XML autorizado
            """,
        responses = {
            @ApiResponse(responseCode = "202", description = "Job aceptado y en procesamiento"),
            @ApiResponse(responseCode = "400", description = "Tipo de proceso inválido")
        })
    @PostMapping(path = "/ejecutar", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> ejecutar(
            @Parameter(description = "Tipo de proceso batch", required = true,
                example = "ENVIAR_SRI",
                allowEmptyValue = false)
            @RequestParam String tipo,
            
            @Parameter(description = "Identificador de quien dispara el job (opcional)")
            @RequestParam(required = false, defaultValue = "api") String triggeredBy) {
        
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            BatchProcessorService.TipoProceso tipoProceso;
            try {
                tipoProceso = BatchProcessorService.TipoProceso.valueOf(tipo.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "Tipo de proceso inválido: " + tipo,
                    "tiposValidos", java.util.Arrays.stream(BatchProcessorService.TipoProceso.values())
                        .map(Enum::name)
                        .toList(),
                    "requestId", requestId
                ));
            }

            var job = batchProcessorService.crearJob(tipoProceso, triggeredBy);
            
            batchProcessorService.ejecutarJob(job);

            log.info("Batch job creado y ejecutado: uuid={} tipo={} [requestId={}]", 
                job.getUuid(), tipo, requestId);

            return ResponseEntity.accepted().body(Map.of(
                "jobUuid", job.getUuid(),
                "tipo", tipo,
                "estado", job.getEstado(),
                "totalDocumentos", job.getTotalDocumentos() != null ? job.getTotalDocumentos() : 0,
                "procesados", job.getProcesados(),
                "exitosos", job.getExitosos(),
                "fallidos", job.getFallidos(),
                "mensaje", job.getMensaje() != null ? job.getMensaje() : "",
                "requestId", requestId
            ));

        } catch (Exception e) {
            log.error("Error ejecutando batch job [requestId={}]", requestId, e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Error ejecutando batch job",
                "detalle", e.getMessage(),
                "requestId", requestId
            ));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Consultar estado de un batch job", description = "Consulta el estado y resultados de un job batch por su UUID")
    @GetMapping("/{uuid}")
    public ResponseEntity<?> consultar(@PathVariable String uuid) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            return batchJobR.findByUuid(uuid)
                .map(job -> {
                    var result = new java.util.LinkedHashMap<String, Object>();
                    result.put("uuid", job.getUuid());
                    result.put("tipoProceso", job.getTipoProceso());
                    result.put("estado", job.getEstado());
                    result.put("totalDocumentos", job.getTotalDocumentos() != null ? job.getTotalDocumentos() : 0);
                    result.put("procesados", job.getProcesados());
                    result.put("exitosos", job.getExitosos());
                    result.put("fallidos", job.getFallidos());
                    result.put("mensaje", job.getMensaje() != null ? job.getMensaje() : "");
                    result.put("triggeredBy", job.getTriggeredBy() != null ? job.getTriggeredBy() : "");
                    result.put("fechaInicio", job.getFechaInicio() != null ? job.getFechaInicio().toString() : "");
                    result.put("fechaFin", job.getFechaFin() != null ? job.getFechaFin().toString() : "");
                    result.put("requestId", requestId);
                    return ResponseEntity.ok((Object) result);
                })
                .orElse(ResponseEntity.notFound().build());

        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Listar jobs batch recientes")
    @GetMapping
    public ResponseEntity<?> listar(
            @Parameter(description = "Cantidad de resultados") @RequestParam(defaultValue = "20") int limit) {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);

        try {
            var jobs = batchJobR.findRecientes(limit);
            return ResponseEntity.ok(Map.of(
                "jobs", jobs.stream().map(job -> Map.of(
                    "uuid", job.getUuid(),
                    "tipoProceso", job.getTipoProceso(),
                    "estado", job.getEstado(),
                    "totalDocumentos", job.getTotalDocumentos() != null ? job.getTotalDocumentos() : 0,
                    "procesados", job.getProcesados(),
                    "exitosos", job.getExitosos(),
                    "fallidos", job.getFallidos(),
                    "fechaInicio", job.getFechaInicio() != null ? job.getFechaInicio().toString() : ""
                )).toList(),
                "total", jobs.size(),
                "requestId", requestId
            ));

        } finally {
            MDC.clear();
        }
    }
}
