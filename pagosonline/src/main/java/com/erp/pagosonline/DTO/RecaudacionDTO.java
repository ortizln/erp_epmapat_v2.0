package com.erp.pagosonline.DTO;

import lombok.*;

import java.math.BigDecimal;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecaudacionDTO {
    private BigDecimal totalpagar;
    private BigDecimal recibo;
    private BigDecimal cambio;
    private Long formapago;
    private BigDecimal valor;

}
