package com.erp.pagosonline.DTO;

import lombok.*;

import java.math.BigDecimal;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDTO {
    private Long idfactura;
    private BigDecimal valor;
    private BigDecimal interes;
}