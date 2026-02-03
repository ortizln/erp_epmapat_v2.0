package com.erp.sri_files.retenciones.service;

import com.erp.sri_files.retenciones.utils.SriRetencionParser;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.*;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;
import java.util.Map;

public class RetencionPdfService {

    public byte[] generarPdfDesdeXmlAutorizado(String xmlAutorizacion) throws Exception {
        var data = SriRetencionParser.parseAutorizacion(xmlAutorizacion);

        // DataSource para la tabla de impuestos
        JRBeanCollectionDataSource impuestosDs = new JRBeanCollectionDataSource(data.impuestos);
        data.params.put("ImpuestosDS", impuestosDs);

        // Compilar jrxml (pon el archivo en src/main/resources/reportes/)
        InputStream jrxml = new ClassPathResource("reportes/retencion_template.jrxml").getInputStream();
        JasperReport report = JasperCompileManager.compileReport(jrxml);

        // No dependemos de BD, todo por params + DS
        JasperPrint print = JasperFillManager.fillReport(report, (Map<String, Object>) data.params, new JREmptyDataSource(1));
        return JasperExportManager.exportReportToPdf(print);
    }
}
