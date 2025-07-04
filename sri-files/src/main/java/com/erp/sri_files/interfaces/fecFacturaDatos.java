package com.erp.sri_files.interfaces;

import lombok.*;

import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class fecFacturaDatos {
    private Long idfactura;
    private String xmlautorizado;
    private LocalDate fechaemision;
}
