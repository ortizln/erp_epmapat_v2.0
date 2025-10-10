package com.erp.reportes_jr.controllers;

import com.erp.reportes_jr.modelo.Reportes;
import com.erp.reportes_jr.services.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.List;

@RestController
@RequestMapping("/reportesjr")

public class ReportesApi {

        @Autowired
        private ReporteService service;

        @PostMapping("/upload")
        public ResponseEntity<?> uploadReporte(
                @RequestParam("nombre") String nombre,
                @RequestParam("descripcion") String descripcion,
                @RequestParam("jrxml") MultipartFile jrxmlFile,
                @RequestParam("jasper") MultipartFile jasperFile) {

            try {
                Reportes reporte = service.guardarReporte(nombre, descripcion, jrxmlFile, jasperFile);
                return ResponseEntity.ok(reporte);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error al guardar el reporte: " + e.getMessage());
            }
        }

        @GetMapping
        public List<Reportes> listarReportes() {
            return service.listarReportes();
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
            return service.obtenerPorId(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> eliminar(@PathVariable Long id) {
            service.eliminarReporte(id);
            return ResponseEntity.ok("Reporte eliminado");
        }
        @GetMapping("/{id}/download/{tipo}")
        public ResponseEntity<?> descargarArchivo(@PathVariable Long id, @PathVariable String tipo) {
            try {
                byte[] archivo = service.descargarArchivo(id, tipo);

                String contentType = "jrxml".equalsIgnoreCase(tipo) ?
                        "application/xml" : "application/octet-stream";

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_" + id + "." + tipo)
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(archivo);

            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Error: " + e.getMessage());
            }
        }
    @PutMapping("/update/{id}")
    public ResponseEntity<?> actualizarReporte(
            @PathVariable Long id,
            @RequestParam(value = "nombre", required = false) String nombre,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "jrxml", required = false) MultipartFile jrxmlFile,
            @RequestParam(value = "jasper", required = false) MultipartFile jasperFile
    ) {
        try {
            Reportes actualizado = service.actualizarReporte(id, nombre, descripcion, jrxmlFile, jasperFile);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    }

