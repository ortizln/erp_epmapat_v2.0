package com.erp.comercializacion.DTO;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InteresFacDTO {
    private Long idfactura;
    private Float subtotal;
    private BigDecimal interes;
    private BigDecimal total;
    private Long formapago;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate feccrea;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate fectransferencia;
    public void setSuma(Float subtotal2) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setSuma'");
    }
}
