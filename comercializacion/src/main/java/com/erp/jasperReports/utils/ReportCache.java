package com.erp.jasperReports.utils;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

@Component
public class ReportCache {
    private final ConcurrentHashMap<String, JasperReport> cache = new ConcurrentHashMap<>();

    public JasperReport getCompiled(String reportName) throws JRException {
        return cache.computeIfAbsent(reportName, rn -> {
            try (InputStream is = new ClassPathResource("reports/" + rn + ".jrxml").getInputStream()) {
                JasperDesign design = JRXmlLoader.load(is);
                return JasperCompileManager.compileReport(design);
            } catch (Exception e) {
                throw new RuntimeException("Error compilando jrxml: " + rn, e);
            }
        });
    }
}
