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
@Table(name = "fec_factura_pagos")
public class Fec_factura_pagos {

    @Id
    private Long idfacturapagos;
    private Long idfactura;
    private String formapago;
    private BigDecimal total;
    private Integer plazo;
    private String unidadtiempo;
}
