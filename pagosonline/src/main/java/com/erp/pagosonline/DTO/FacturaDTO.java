package com.erp.pagosonline.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Long cuenta;
    private BigDecimal total;
    private String responsablepago;
    private List<Long> facturas;
}