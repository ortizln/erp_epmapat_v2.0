package com.erp.DTO;

import java.time.LocalDate;

public record RecalculoRequest(
        LocalDate fechaCorte,   // si es null, se toma LocalDate.now()
        Integer lagMeses        // si es null, por defecto 1 (calcula hasta mes anterior)
) {}