package com.erp.DTO;

import lombok.Data;

import java.math.BigDecimal;

// Resultados por factura (todo en memoria)
public record CalculoDetalle(
        BigDecimal ap,       // Agua Potable
        BigDecimal alc,      // Alcantarillado
        BigDecimal san,      // Saneamiento
        BigDecimal cf,       // Conservación de fuentes
        BigDecimal exc,      // Excedente (si aplica)
        BigDecimal multa,    // Multas
        BigDecimal total     // Suma
) {}