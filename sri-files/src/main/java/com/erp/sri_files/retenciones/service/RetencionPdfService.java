package com.erp.sri_files.retenciones.service;

import com.erp.sri_files.retenciones.utils.SriRetencionParser;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

@Service
public class RetencionPdfService {

    public byte[] generarPdfDesdeXmlAutorizado(String xmlAutorizacion) throws Exception {
        var data = SriRetencionParser.parseAutorizacion(xmlAutorizacion);

        // OJO: si tu JRXML usa la lista como datasource principal NO necesitas meterla en params.
        // Si tu JRXML tiene una tabla que usa un datasetRun con $P{ImpuestosDS}, entonces sí.
        JRBeanCollectionDataSource impuestosDs = new JRBeanCollectionDataSource(data.impuestos);
        data.params.put("ImpuestosDS", impuestosDs);

        InputStream jrxml = new ClassPathResource("reports/retencion_template.jrxml")
                .getInputStream();

        JasperReport report = JasperCompileManager.compileReport(jrxml);

        // Si tu reporte usa datasetRun con $P{ImpuestosDS}, el datasource principal puede ser vacío:
        JasperPrint print = JasperFillManager.fillReport(
                report,
                (Map<String, Object>) data.params,
                new JREmptyDataSource(1)
        );

        return JasperExportManager.exportReportToPdf(print);
    }
}
