package com.erp.sri_files.monitoring;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/logs")
@Tag(name = "Logs", description = "Consulta de logs del sistema")
public class LogController {

    private static final Logger log = LoggerFactory.getLogger(LogController.class);

    @Value("${app.logs.path:logs/sri-files.log}")
    private String logFilePath;

    @Operation(summary = "Consultar logs", description = "Retorna las últimas líneas del archivo de log con filtros opcionales")
    @GetMapping
    public ResponseEntity<?> consultar(
            @Parameter(description = "Número de últimas líneas") @RequestParam(defaultValue = "200") int lines,
            @Parameter(description = "Nivel: ERROR, WARN, INFO, DEBUG") @RequestParam(required = false) String level,
            @Parameter(description = "Buscar texto") @RequestParam(required = false) String search,
            @Parameter(description = "Filtrar por requestId") @RequestParam(required = false) String requestId) {

        try {
            Path path = Path.of(logFilePath);
            if (!Files.exists(path)) {
                return ResponseEntity.ok(Map.of(
                    "lines", List.of(),
                    "total", 0,
                    "file", logFilePath,
                    "timestamp", java.time.LocalDateTime.now().toString()
                ));
            }

            List<String> allLines;
            try (Stream<String> stream = Files.lines(path)) {
                allLines = stream.collect(Collectors.toList());
            }

            Stream<String> filtered = allLines.stream();

            if (level != null && !level.isBlank()) {
                String lvl = level.toUpperCase();
                filtered = filtered.filter(l -> l.contains(" " + lvl + " "));
            }
            if (search != null && !search.isBlank()) {
                String s = search.toLowerCase();
                filtered = filtered.filter(l -> l.toLowerCase().contains(s));
            }
            if (requestId != null && !requestId.isBlank()) {
                filtered = filtered.filter(l -> l.contains(requestId));
            }

            List<String> result = filtered
                .skip(Math.max(0, allLines.size() - lines))
                .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "lines", result,
                "total", allLines.size(),
                "filtered", result.size(),
                "file", logFilePath,
                "timestamp", java.time.LocalDateTime.now().toString()
            ));

        } catch (Exception e) {
            log.error("Error consultando logs", e);
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @Operation(summary = "Archivos de log disponibles", description = "Lista archivos .log en el directorio")
    @GetMapping("/files")
    public ResponseEntity<?> archivos() {
        try {
            Path logDir = Path.of(logFilePath).getParent();
            if (logDir == null || !Files.exists(logDir)) {
                return ResponseEntity.ok(Map.of("files", List.of(), "directory", String.valueOf(logDir)));
            }

            List<Map<String, Object>> files;
            try (Stream<Path> stream = Files.list(logDir)) {
                files = stream
                    .filter(p -> p.toString().endsWith(".log"))
                    .sorted(Comparator.reverseOrder())
                    .map(p -> {
                        try {
                            return Map.<String, Object>of(
                                "name", p.getFileName().toString(),
                                "size", Files.size(p),
                                "lastModified", Files.getLastModifiedTime(p).toInstant().toString()
                            );
                        } catch (IOException e) {
                            return Map.<String, Object>of("name", p.getFileName().toString(), "error", e.getMessage());
                        }
                    })
                    .toList();
            }
            return ResponseEntity.ok(Map.of("files", files, "directory", logDir.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }
}
