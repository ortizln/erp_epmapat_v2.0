package com.erp.reportes_jr.DTO;
import lombok.*;

import java.util.List;
@Data
public class JasperDTO {
    private String reportName;
    private List<ReportParameterDTO> parameters;
    private String extension;

    public Long getReporteId() {
        return reporteId;
    }

    public void setReporteId(Long reporteId) {
        this.reporteId = reporteId;
    }

    private Long reporteId;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public List<ReportParameterDTO> getParameters() {
        return parameters;
    }

    public void setParameters(List<ReportParameterDTO> parameters) {
        this.parameters = parameters;
    }
}