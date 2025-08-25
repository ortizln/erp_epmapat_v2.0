package com.erp.reportes_jr;

import com.erp.reportes_jr.modals.Reportes;
import com.erp.reportes_jr.repositories.ReportesR;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.sql.Connection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@Service
public class ReportesService {
    @Autowired
    private ReportesR reportesR;
    @Autowired
    private DataSource dataSource;
    public Reportes guardarReporte(String nombre, MultipartFile archivo) throws IOException {
        Reportes reporte = new Reportes();
        reporte.setNombre(nombre);
        reporte.setArchivo(archivo.getBytes());
        return reportesR.save(reporte);
    }


    public byte[] generarReporte(String nombreReporte, Map<String, Object> parametros) throws Exception {
        // Buscar el reporte
        Reportes reporte = reportesR.findByNombre(nombreReporte)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));

        // Cargar el .jasper desde bytes
        try (ByteArrayInputStream bais = new ByteArrayInputStream(reporte.getArchivo())) {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(bais);

            // Llenar el reporte con datos desde la BD
            try (Connection conn = dataSource.getConnection()) {
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, conn);

                // Exportar a PDF (puede ser XLS, DOCX, etc.)
                return JasperExportManager.exportReportToPdf(jasperPrint);
            }
        }
    }
}
