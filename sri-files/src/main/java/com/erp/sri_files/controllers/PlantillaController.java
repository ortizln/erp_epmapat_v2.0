package com.erp.sri_files.controllers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plantillas")
@Tag(name = "Plantillas JRXML", description = "Gestión de plantillas JasperReports para generación de PDFs")
public class PlantillaController {

    private static final Logger log = LoggerFactory.getLogger(PlantillaController.class);

    @Value("${app.reports.path:/reports}")
    private String reportsPath;

    @Operation(summary = "Listar plantillas JRXML", description = "Lista todos los archivos .jrxml disponibles en el directorio de reportes",
        responses = {
            @ApiResponse(responseCode = "200", description = "Listado de plantillas")
        })
    @GetMapping
    public ResponseEntity<?> listar() {
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        try {
            Path dir = Path.of(reportsPath);
            if (!Files.exists(dir)) {
                return ResponseEntity.ok(Map.of(
                    "directorio", reportsPath,
                    "plantillas", List.of(),
                    "total", 0
                ));
            }
            List<Map<String, Object>> plantillas;
            try (Stream<Path> files = Files.list(dir)) {
                plantillas = files
                    .filter(p -> p.toString().endsWith(".jrxml"))
                    .sorted()
                    .map(p -> {
                        try {
                            return Map.<String, Object>of(
                                "nombre", p.getFileName().toString(),
                                "tamanio", Files.size(p),
                                "ultimaModificacion", Files.getLastModifiedTime(p).toInstant().toString()
                            );
                        } catch (IOException e) {
                            return Map.<String, Object>of(
                                "nombre", p.getFileName().toString(),
                                "error", e.getMessage()
                            );
                        }
                    })
                    .toList();
            }
            return ResponseEntity.ok(Map.of(
                "directorio", reportsPath,
                "plantillas", plantillas,
                "total", plantillas.size()
            ));
        } catch (Exception e) {
            log.error("Error listando plantillas", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        } finally {
            MDC.clear();
        }
    }

    @Operation(summary = "Obtener contenido de una plantilla", description = "Retorna el contenido XML del archivo .jrxml",
        responses = {
            @ApiResponse(responseCode = "200", description = "Contenido de la plantilla"),
            @ApiResponse(responseCode = "404", description = "Plantilla no encontrada")
        })
    @GetMapping("/{nombre}")
    public ResponseEntity<?> obtener(
            @Parameter(description = "Nombre del archivo .jrxml", required = true, example = "factura_template.jrxml")
            @PathVariable String nombre) {
        try {
            Path archivo = Path.of(reportsPath, nombre);
            if (!Files.exists(archivo) || !nombre.endsWith(".jrxml")) {
                return ResponseEntity.status(404).body(Map.of("error", "Plantilla no encontrada: " + nombre));
            }
            String contenido = Files.readString(archivo, StandardCharsets.UTF_8);
            return ResponseEntity.ok(Map.of(
                "nombre", nombre,
                "contenido", contenido,
                "tamanio", Files.size(archivo)
            ));
        } catch (Exception e) {
            log.error("Error leyendo plantilla {}", nombre, e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Crear nueva plantilla JRXML", description = "Crea un nuevo archivo .jrxml en el directorio de reportes",
        responses = {
            @ApiResponse(responseCode = "200", description = "Plantilla creada"),
            @ApiResponse(responseCode = "409", description = "Ya existe una plantilla con ese nombre")
        })
    @PostMapping
    public ResponseEntity<?> crear(
            @Parameter(description = "Nombre del archivo .jrxml", required = true, example = "retencion_template.jrxml")
            @RequestParam String nombre,
            @Parameter(description = "Contenido XML del archivo .jrxml", required = true)
            @RequestBody String contenido) {
        try {
            if (!nombre.endsWith(".jrxml")) {
                nombre = nombre + ".jrxml";
            }
            Path archivo = Path.of(reportsPath, nombre);
            if (Files.exists(archivo)) {
                return ResponseEntity.status(409).body(Map.of(
                    "error", "Ya existe una plantilla con ese nombre",
                    "nombre", nombre
                ));
            }
            Files.createDirectories(archivo.getParent());
            Files.writeString(archivo, contenido, StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
            log.info("Plantilla creada: {}", nombre);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Plantilla creada correctamente",
                "nombre", nombre,
                "tamanio", Files.size(archivo)
            ));
        } catch (Exception e) {
            log.error("Error creando plantilla {}", nombre, e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Actualizar plantilla JRXML", description = "Actualiza el contenido de un archivo .jrxml existente",
        responses = {
            @ApiResponse(responseCode = "200", description = "Plantilla actualizada"),
            @ApiResponse(responseCode = "404", description = "Plantilla no encontrada")
        })
    @PutMapping("/{nombre}")
    public ResponseEntity<?> actualizar(
            @Parameter(description = "Nombre del archivo .jrxml", required = true)
            @PathVariable String nombre,
            @Parameter(description = "Nuevo contenido XML", required = true)
            @RequestBody String contenido) {
        try {
            Path archivo = Path.of(reportsPath, nombre);
            if (!Files.exists(archivo) || !nombre.endsWith(".jrxml")) {
                return ResponseEntity.status(404).body(Map.of("error", "Plantilla no encontrada: " + nombre));
            }
            Files.writeString(archivo, contenido, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("Plantilla actualizada: {}", nombre);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Plantilla actualizada correctamente",
                "nombre", nombre,
                "tamanio", Files.size(archivo)
            ));
        } catch (Exception e) {
            log.error("Error actualizando plantilla {}", nombre, e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar plantilla JRXML", description = "Elimina un archivo .jrxml del directorio de reportes",
        responses = {
            @ApiResponse(responseCode = "200", description = "Plantilla eliminada"),
            @ApiResponse(responseCode = "404", description = "Plantilla no encontrada")
        })
    @DeleteMapping("/{nombre}")
    public ResponseEntity<?> eliminar(
            @Parameter(description = "Nombre del archivo .jrxml", required = true)
            @PathVariable String nombre) {
        try {
            Path archivo = Path.of(reportsPath, nombre);
            if (!Files.exists(archivo) || !nombre.endsWith(".jrxml")) {
                return ResponseEntity.status(404).body(Map.of("error", "Plantilla no encontrada: " + nombre));
            }
            Files.delete(archivo);
            log.info("Plantilla eliminada: {}", nombre);
            return ResponseEntity.ok(Map.of(
                "mensaje", "Plantilla eliminada correctamente",
                "nombre", nombre
            ));
        } catch (Exception e) {
            log.error("Error eliminando plantilla {}", nombre, e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
