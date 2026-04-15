package com.erp.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class TrainingPlanRequest extends AuditMetadataRequest {

    private Long employeeId;

    @NotBlank(message = "area es obligatoria")
    private String area;

    @NotBlank(message = "title es obligatorio")
    private String title;

    private String description;

    @NotNull(message = "startDate es obligatoria")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull(message = "hours es obligatorio")
    private BigDecimal hours;

    private BigDecimal cost;

    @NotBlank(message = "status es obligatorio")
    private String status;
}

