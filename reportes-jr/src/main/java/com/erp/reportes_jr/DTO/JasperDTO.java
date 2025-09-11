package com.erp.reportes_jr.DTO;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class JasperDTO {
    private String reportName;
    private Map<String, Object> parameters = new HashMap<>();
    private String extencion;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public String getExtencion() {
        return extencion;
    }

    public void setExtencion(String extencion) {
        this.extencion = extencion;
    }
}
