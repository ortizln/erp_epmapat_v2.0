package com.erp.DTO;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class ValidarRecargosRequest {
    private Long idemision;
    private LocalDate fecha;
    private List<Item> items;

    @Data
    public static class Item {
        private Long idabonado;
        private Integer tipo;
    }
}
