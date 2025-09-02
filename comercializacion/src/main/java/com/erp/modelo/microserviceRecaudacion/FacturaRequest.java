package com.erp.modelo.microserviceRecaudacion;
import java.util.List;

import com.erp.modelo.Recaudacion;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class FacturaRequest {
    private List<Long> facturas;
    private Long autentification;
    private Recaudacion recaudacion;

}