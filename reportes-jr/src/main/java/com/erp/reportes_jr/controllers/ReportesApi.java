package com.erp.reportes_jr.controllers;

import com.erp.reportes_jr.modelo.Reportes;
import com.erp.reportes_jr.services.ReporteService;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.ByteArrayInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportesjr")
@CrossOrigin("*")
public class ReportesApi {

    @Autowired
    private ReporteService reportesService;

    @PostMapping("/posting")
    public ResponseEntity<Reportes> _guardarReporte(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("jrxml") MultipartFile jrxml,
            @RequestParam("jasper") MultipartFile jasper
    ) {
        try {
            Reportes reporte = new Reportes();
            reporte.setNombre(nombre);
            reporte.setDescripcion(descripcion);
            reporte.setArchivoJrxml(jrxml.getBytes());
            reporte.setArchivoJasper(jasper.getBytes());

            Reportes guardado = reportesService.guardar(reporte);
            return ResponseEntity.ok(guardado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<Reportes> guardarReporte(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("jrxml") MultipartFile jrxml,
            @RequestParam("jasper") MultipartFile jasper
    ) {
        try {
            Reportes reporte = new Reportes();
            reporte.setNombre(nombre);
            reporte.setDescripcion(descripcion);
            reporte.setArchivoJrxml(jrxml.getBytes());
            reporte.setArchivoJasper(jasper.getBytes());

            // 1️⃣ Compilar el JRXML en memoria
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    new ByteArrayInputStream(jrxml.getBytes())
            );

            // 2️⃣ Obtener los parámetros del reporte y su tipo
            Map<String, Object> parametrosMap = new LinkedHashMap<>();
            for (JRParameter param : jasperReport.getParameters()) {
                if (!param.isSystemDefined()) {
                    // Guardamos el nombre del parámetro y su tipo (className)
                    parametrosMap.put(param.getName(), param.getValueClassName());
                }
            }

            // 3️⃣ Guardamos los parámetros en la columna JSONB
            reporte.setParametros(parametrosMap);

            // 4️⃣ Guardar en la DB
            Reportes guardado = reportesService.guardar(reporte);
            return ResponseEntity.ok(guardado);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Reportes>> listarTodos() {
        return ResponseEntity.ok(reportesService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reportes> obtenerPorId(@PathVariable Long id) {
        return reportesService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        reportesService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}