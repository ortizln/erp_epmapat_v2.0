package com.erp.sri.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaRequest {
    private List<Long> facturas;
    private Long autentification;
    private Recaudacion recaudacion;

}
