package com.erp.rrhh.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotBlank(message = "updatedBy es obligatorio")
    private String updatedBy;
}

