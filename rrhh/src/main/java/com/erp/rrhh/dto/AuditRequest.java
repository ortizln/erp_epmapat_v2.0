package com.erp.rrhh.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AuditRequest extends AuditMetadataRequest {

    @NotBlank(message = "auditType es obligatorio")
    private String auditType;

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotBlank(message = "findings es obligatorio")
    private String findings;

    @NotBlank(message = "actionPlan es obligatorio")
    private String actionPlan;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotNull(message = "auditDate es obligatoria")
    private LocalDate auditDate;

    @NotBlank(message = "responsible es obligatorio")
    private String responsible;
}

