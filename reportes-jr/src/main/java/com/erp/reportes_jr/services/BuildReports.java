package com.erp.reportes_jr.services;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

import com.erp.reportes_jr.DTO.JasperDTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.*;
import org.springframework.stereotype.Service;

@Service
public class BuildReports {
    // PDF
    public ByteArrayOutputStream buildPdfReport(JasperDTO dto, Connection conn) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/" + dto.getReportName() + ".jrxml")
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dto.getParameters(), conn);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
        return outputStream;
    }

    // XLSX
    public ByteArrayOutputStream buildXlsxReport(JasperDTO dto, Connection conn) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/" + dto.getReportName() + ".jrxml")
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dto.getParameters(), conn);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
        configuration.setDetectCellType(true);   // Detecta tipo de celda
        configuration.setCollapseRowSpan(false);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
        return outputStream;
    }

    // CSV
    public ByteArrayOutputStream buildCsvReport(JasperDTO dto, Connection conn) throws JRException {
        JasperReport jasperReport = JasperCompileManager.compileReport(
                getClass().getResourceAsStream("/reports/" + dto.getReportName() + ".jrxml")
        );
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, dto.getParameters(), conn);

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
