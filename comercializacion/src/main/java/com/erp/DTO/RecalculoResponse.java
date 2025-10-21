package com.erp.DTO;

public record RecalculoResponse(
        int status,
        int totalFacturas,
        String desde,          // YearMonth mínimo (string yyyy-MM)
        String hasta,          // YearMonth corte (string yyyy-MM)
        String message
) {}