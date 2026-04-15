package com.erp.rrhh.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AuditMetadataRequest {

    @NotBlank(message = "createdBy es obligatorio")
    private String createdBy;

    @NotBlank(message = "updatedBy es obligatorio")
    private String updatedBy;
}

