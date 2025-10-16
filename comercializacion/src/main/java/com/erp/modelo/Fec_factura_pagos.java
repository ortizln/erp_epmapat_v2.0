package com.erp.modelo;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
