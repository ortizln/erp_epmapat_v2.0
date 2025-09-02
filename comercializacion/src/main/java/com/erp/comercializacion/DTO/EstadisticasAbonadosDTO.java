package com.erp.comercializacion.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasAbonadosDTO {
    private String descripcion;
    private Integer ncuentas;
    private Integer estado;
}
