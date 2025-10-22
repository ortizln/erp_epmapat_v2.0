package com.erp.reportes_jr.controllers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import com.erp.reportes_jr.DTO.JasperDTO;
import com.erp.reportes_jr.DTO.ReportParameterDTO;
import com.erp.reportes_jr.services.BuildReports;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/jasperReports")
@CrossOrigin("*")
public class BuildReportsApi {
    @Autowired
    private BuildReports buildReports;
    @Autowired
    private DataSource dataSource;

    @GetMapping("/reportes")
    public ResponseEntity<String> reportesGetNotAllowed() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body("El método GET no está permitido en este endpoint. Usa POST.");
    }



    private Object parseDateToSQLType(String value) throws ParseException {
        String[] formats = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd HH:mm",
                "yyyy-MM-dd"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                Date parsed = sdf.parse(value.trim());
                if (format.contains("HH")) {
                    return new java.sql.Timestamp(parsed.getTime());
                } else {
                    return new java.sql.Date(parsed.getTime());
                }
            } catch (ParseException ignored) {
            }
        }

        throw new IllegalArgumentException("Fecha inválida: " + value);
    }

    private java.sql.Time parseToSqlTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Time string cannot be null or empty");
        }

        String[] formats = {
                "HH:mm:ss",
                "HH:mm"
        };

        for (String format : formats) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.setLenient(false);
                Date parsed = sdf.parse(timeStr.trim());
                return new java.sql.Time(parsed.getTime());
            } catch (ParseException e) {
                // Try next format
            }
        }

        throw new IllegalArgumentException("Invalid time format: '" + timeStr +
                "'. Expected formats: HH:mm:ss or HH:mm");
    }

    private Object normalizeParameterValue(String key, Object value) {
        if (value instanceof Integer) {
            return value;
        } else if (value instanceof Long) {
            Long longVal = (Long) value;
            if (longVal >= Integer.MIN_VALUE && longVal <= Integer.MAX_VALUE) {
                return longVal.intValue();
            } else {
                throw new IllegalArgumentException("El valor Long excede el rango de Integer");
            }
        } else if (value instanceof java.util.Date) {
            return value; // Devuelve la fecha tal cual
        } else if (value instanceof String) {
            // 🔥 Mantener como String, no convertir a Integer
            return value;
        }
        return value;
    }

    //REPORTES DESDE BASE DE DATOS



    @PostMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarPdf(@RequestBody JasperDTO jasperDTO) throws JRException, SQLException {
        ByteArrayOutputStream outputStream = buildReports.buildPdfReport(jasperDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(outputStream.toByteArray());
    }

    @PostMapping("/{id}/xlsx")
    public ResponseEntity<byte[]> descargarXlsx(@RequestBody JasperDTO jasperDTO) throws JRException, SQLException {
        ByteArrayOutputStream outputStream = buildReports.buildXlsxReport(jasperDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(outputStream.toByteArray());
    }

    @PostMapping("/{id}/csv")
    public ResponseEntity<byte[]> descargarCsv(@RequestBody JasperDTO jasperDTO) throws JRException, SQLException {
        ByteArrayOutputStream outputStream = buildReports.buildCsvReport(jasperDTO);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(outputStream.toByteArray());
    }

    @PostMapping("/descargar")
    public ResponseEntity<byte[]> descargarReporte(@RequestBody JasperDTO jasperDTO) {
        try {
            ByteArrayOutputStream outputStream;
            String filename;
            MediaType mediaType;

            // Generar el reporte llamando al servicio; el servicio se encarga de la conversión de parámetros
            switch (jasperDTO.getExtension().toLowerCase()) {
                case "pdf":
                    outputStream = buildReports.buildPdfReport(jasperDTO);
                    filename = jasperDTO.getReportName() + ".pdf";
                    mediaType = MediaType.APPLICATION_PDF;
                    break;

                case "xlsx":
                    outputStream = buildReports.buildXlsxReport(jasperDTO);
                    filename = jasperDTO.getReportName() + ".xlsx";
                    mediaType = MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    );
                    break;

                case "csv":
                    outputStream = buildReports.buildCsvReport(jasperDTO);
                    filename = jasperDTO.getReportName() + ".csv";
                    mediaType = MediaType.TEXT_PLAIN;
                    break;

                default:
                    return ResponseEntity.badRequest().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                    .contentType(mediaType)
                    .body(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public Map<String, Object> convertirParametros(List<ReportParameterDTO> parametros) {
        Map<String, Object> result = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (ReportParameterDTO p : parametros) {
            Object valorConvertido = null;
            try {
                switch (p.getType()) {
                    case "java.lang.String":
                        valorConvertido = String.valueOf(p.getValue());
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
                        valorConvertido = sdf.parse(p.getValue().toString());
                        break;

                    default:
                        valorConvertido = p.getValue(); // fallback
                }
            } catch (Exception e) {
                throw new RuntimeException("Error convirtiendo parámetro: " + p.getName(), e);
            }

            result.put(p.getName(), valorConvertido);
        }

        return result;
    }


}