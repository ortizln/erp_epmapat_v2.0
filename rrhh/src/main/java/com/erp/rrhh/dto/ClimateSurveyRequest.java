package com.erp.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ClimateSurveyRequest extends AuditMetadataRequest {

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotBlank(message = "area es obligatoria")
    private String area;

    @NotNull(message = "startDate es obligatoria")
    private LocalDate startDate;

    @NotNull(message = "endDate es obligatoria")
    private LocalDate endDate;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotNull(message = "participationRate es obligatorio")
    private BigDecimal participationRate;

    @NotNull(message = "satisfactionScore es obligatorio")
    private BigDecimal satisfactionScore;
}

