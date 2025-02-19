package com.erp.recaudacion.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class FacturaRequest {
    private List<Long> facturas;
    private Long autentification;
    private Recaudacion recaudacion;

}
