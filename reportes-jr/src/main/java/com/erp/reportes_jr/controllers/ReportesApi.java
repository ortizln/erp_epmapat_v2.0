package com.erp.reportes_jr.controllers;

import com.erp.reportes_jr.ReportesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/jasper")
public class ReportesApi {
    @Autowired
    private ReportesService reportesService;
    @PostMapping("/upload")
    public ResponseEntity<String> subirReporte(
            @RequestParam("nombre") String nombre,
            @RequestParam("file") MultipartFile file) {
        try {
            reportesService.guardarReporte(nombre, file);
            return ResponseEntity.ok("Reporte guardado correctamente con nombre: " + nombre);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar el reporte: " + e.getMessage());
        }
    }
}
