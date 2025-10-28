package com.erp.pagosonline.DTO;

import lombok.*;

import java.sql.Timestamp;
import java.util.List;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequestDTO {
    //private List<Long> facturas;
    private Long autentification;
    private RecaudacionDTO recaudacion;
    private Long cuenta;
    private String secuencial;
    private Timestamp fechacompensacion;
}
