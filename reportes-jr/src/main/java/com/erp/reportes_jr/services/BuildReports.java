package com.erp.reportes_jr.services;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.erp.reportes_jr.DTO.JasperDTO;
import com.erp.reportes_jr.DTO.ReportParameterDTO;
import com.erp.reportes_jr.modelo.Reportes;
import com.erp.reportes_jr.repositories.ReportesR;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class BuildReports {
    @Autowired
    private ReportesR repo;
    @Autowired
    private ReporteService reportesService;
    @Autowired
    private DataSource dataSource; // Para la conexión a la DB

    // PDF
    // PDF
    public ByteArrayOutputStream _buildPdfReport(JasperDTO dto, Connection conn) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/" + dto.getReportName() + ".jrxml")
        );

        Map<String, Object> params = convertParams(dto.getParameters());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        return outputStream;
    }

    // XLSX
    public ByteArrayOutputStream _buildXlsxReport(JasperDTO dto, Connection conn) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/" + dto.getReportName() + ".jrxml")
        );

        Map<String, Object> params = convertParams(dto.getParameters());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        return outputStream;
    }

    // CSV
    public ByteArrayOutputStream _buildCsvReport(JasperDTO dto, Connection conn) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/" + dto.getReportName() + ".jrxml")
        );

        Map<String, Object> params = convertParams(dto.getParameters());

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        JRCsvExporter exporter = new JRCsvExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(writer));

        SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        return outputStream;
    }

    //Reportes desde base de datos

    public Reportes buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Reporte no encontrado"));
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public ByteArrayOutputStream buildPdfReport(JasperDTO dto) throws JRException, SQLException {
        Reportes reporte = reportesService.findByNombre(dto.getReportName());
        Map<String, Object> params = convertParams(dto.getParameters());

        try (Connection conn = getConnection()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    new ByteArrayInputStream(reporte.getArchivoJrxml())
            );
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return outputStream;
        }
    }

    public ByteArrayOutputStream buildXlsxReport(JasperDTO dto) throws JRException, SQLException {
        Reportes reporte = reportesService.findByNombre(dto.getReportName());
        Map<String, Object> params = convertParams(dto.getParameters());

        try (Connection conn = getConnection()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    new ByteArrayInputStream(reporte.getArchivoJrxml())
            );
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setDetectCellType(true);
            configuration.setCollapseRowSpan(false);
            exporter.setConfiguration(configuration);

            exporter.exportReport();
            return outputStream;
        }
    }

    public ByteArrayOutputStream buildCsvReport(JasperDTO dto) throws JRException, SQLException {
        Reportes reporte = reportesService.findByNombre(dto.getReportName());
        Map<String, Object> params = convertParams(dto.getParameters());

        try (Connection conn = getConnection()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(
                    new ByteArrayInputStream(reporte.getArchivoJrxml())
            );
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, conn);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

            JRCsvExporter exporter = new JRCsvExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleWriterExporterOutput(writer));

            SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
            exporter.setConfiguration(configuration);

            exporter.exportReport();
            return outputStream;
        }
    }


    private Map<String, Object> convertParams(List<ReportParameterDTO> parametros) throws JRException {
        Map<String, Object> params = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (parametros != null) {
            for (ReportParameterDTO p : parametros) {
                if (p.getValue() == null) continue;

                Object valorConvertido;
                switch (p.getType()) {
                    case "java.lang.String":
                        valorConvertido = p.getValue().toString();
                        break;
                    case "java.lang.Integer":
                        valorConvertido = Integer.valueOf(p.getValue().toString());
                        break;
                    case "java.lang.Long":
                        valorConvertido = Long.valueOf(p.getValue().toString());
                        break;
                    case "java.lang.Boolean":
                        valorConvertido = Boolean.valueOf(p.getValue().toString());
                        break;
                    case "java.util.Date":
                        try {
                            valorConvertido = sdf.parse(p.getValue().toString());
                        } catch (Exception e) {
                            throw new JRException("Error parseando fecha para parámetro " + p.getName(), e);
                        }
                        break;
                    default:
                        valorConvertido = p.getValue();
                }
                params.put(p.getName(), valorConvertido);
            }
        }
        return params;
    }
}