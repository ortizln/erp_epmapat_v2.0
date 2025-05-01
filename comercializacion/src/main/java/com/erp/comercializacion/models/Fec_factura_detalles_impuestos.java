package com.erp.comercializacion.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fec_factura_detalles_impuestos")
public class Fec_factura_detalles_impuestos {
    @Id
    private Long idfacturadetalleimpuestos;
    private Long idfacturadetalle;
    private String codigoimpuesto;
    private String codigoporcentaje;
    private BigDecimal baseimponible;
}
