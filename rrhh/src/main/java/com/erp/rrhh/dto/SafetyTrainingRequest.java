package com.erp.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SafetyTrainingRequest extends AuditMetadataRequest {

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotBlank(message = "area es obligatoria")
    private String area;

    @NotNull(message = "trainingDate es obligatoria")
    private LocalDate trainingDate;

    @NotNull(message = "hours es obligatorio")
    private BigDecimal hours;

    @NotNull(message = "attendees es obligatorio")
    private Integer attendees;

    @NotBlank(message = "status es obligatorio")
    private String status;
}

