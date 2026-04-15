package com.erp.rrhh.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PolicyRequest extends AuditMetadataRequest {

    @NotBlank(message = "code es obligatorio")
    private String code;

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotBlank(message = "version es obligatoria")
    private String version;

    @NotNull(message = "effectiveDate es obligatoria")
    private LocalDate effectiveDate;

    @NotBlank(message = "status es obligatorio")
    private String status;

    private String description;
}

