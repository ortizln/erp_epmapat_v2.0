package com.erp.reportes_jr.services;

import com.erp.reportes_jr.modelo.Reportes;
import com.erp.reportes_jr.repositories.ReportesR;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Service
public class ReporteService {
@Autowired
private ReportesR repo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Reportes guardarReporte(String nombre, String descripcion, MultipartFile jrxmlFile, MultipartFile jasperFile) throws Exception {
        if (repo.existsByNombre(nombre)) {
            throw new RuntimeException("Ya existe un reporte con ese nombre");
        }
        Reportes reporte = new Reportes();
        reporte.setNombre(nombre);
        reporte.setDescripcion(descripcion);
        reporte.setArchivoJrxml(jrxmlFile.getBytes());
        reporte.setArchivoJasper(jasperFile.getBytes());
        // Extraer parámetros del Jasper compilado
        try (ByteArrayInputStream jasperStream = new ByteArrayInputStream(jasperFile.getBytes())) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
            List<Map<String, Object>> parametros = new ArrayList<>();
            for (JRParameter p : jasperReport.getParameters()) {
                if (!p.isSystemDefined()) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("nombre", p.getName());
                    param.put("tipo", p.getValueClassName());
                    parametros.add(param);
                }
            }
            reporte.setParametros(new ObjectMapper().writeValueAsString(parametros));
        }
        return repo.save(reporte);
    }

    public List<Reportes> listarReportes() {
        return repo.findAll();
    }

    public Optional<Reportes> obtenerPorId(Long id) {
        return repo.findById(id);
    }

    public void eliminarReporte(Long id) {
        repo.deleteById(id);
    }
    public byte[] descargarArchivo(Long id, String tipo) throws Exception {
        Reportes reporte = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        if ("jrxml".equalsIgnoreCase(tipo)) {
            return reporte.getArchivoJrxml();
        } else if ("jasper".equalsIgnoreCase(tipo)) {
            return reporte.getArchivoJasper();
        } else {
            throw new IllegalArgumentException("Tipo de archivo no válido: " + tipo);
        }
    }
    @Transactional
    public Reportes actualizarReporte(Long id,
                                      String nombre,
                                      String descripcion,
                                      MultipartFile jrxmlFile,
                                      MultipartFile jasperFile) throws Exception {
        Reportes reporte = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado"));

        // Actualizamos nombre y descripción si llegan
        if (nombre != null && !nombre.isEmpty()) {
            reporte.setNombre(nombre);
        }
        if (descripcion != null && !descripcion.isEmpty()) {
            reporte.setDescripcion(descripcion);
        }

        // Si subieron un nuevo JRXML, reemplazar
        if (jrxmlFile != null && !jrxmlFile.isEmpty()) {
            reporte.setArchivoJrxml(jrxmlFile.getBytes());
        }

        // Si subieron un nuevo JASPER, reemplazar y recalcular parámetros
        if (jasperFile != null && !jasperFile.isEmpty()) {
            reporte.setArchivoJasper(jasperFile.getBytes());

            // Extraer parámetros dinámicamente
            InputStream jasperStream = new ByteArrayInputStream(jasperFile.getBytes());
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            List<Map<String, Object>> parametros = new ArrayList<>();
            for (JRParameter p : jasperReport.getParameters()) {
                if (!p.isSystemDefined()) {
                    Map<String, Object> param = new HashMap<>();
                    param.put("nombre", p.getName());
                    param.put("tipo", p.getValueClassName());
                    parametros.add(param);
                }
            }
            reporte.setParametros(new ObjectMapper().writeValueAsString(parametros));
        }

        return repo.save(reporte);
    }

}
