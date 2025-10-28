package com.erp.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public record PreviewResponse(
        int status,
        Long idFactura,
        LocalDate fechaCorte,
        BigDecimal interesCalculado
) {}