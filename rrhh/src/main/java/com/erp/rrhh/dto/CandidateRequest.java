package com.erp.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CandidateRequest extends AuditMetadataRequest {

    @NotNull(message = "vacancyId es obligatorio")
    private UUID vacancyId;

    @NotBlank(message = "firstName es obligatorio")
    private String firstName;

    @NotBlank(message = "lastName es obligatorio")
    private String lastName;

    @NotBlank(message = "identification es obligatorio")
    private String identification;

    @Email(message = "email no es válido")
    @NotBlank(message = "email es obligatorio")
    private String email;

    private String phone;

    @NotBlank(message = "stage es obligatoria")
    private String stage;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotNull(message = "appliedAt es obligatorio")
    private LocalDate appliedAt;

    private BigDecimal score;
}

