package com.erp.comercializacion.DTO;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RemiDTO {
    private Long idfactura;
    private String descripcion;
    private LocalDate feccrea;
    private BigDecimal total;
    private BigDecimal intereses;
    private String nrofactura;
}
