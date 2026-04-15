package com.erp.rrhh.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class OnboardingRequest extends AuditMetadataRequest {

    private Long employeeId;
    private UUID candidateId;

    @NotNull(message = "startDate es obligatoria")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotBlank(message = "owner es obligatorio")
    private String owner;

    private String notes;
}

