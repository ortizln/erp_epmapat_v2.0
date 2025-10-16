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
@Table(name = "fec_factura_detalles")
public class Fec_factura_detalles {
	@Id
	private Long idfacturadetalle;
	private Long idfactura;
	private String codigoprincipal;
	private String descripcion;
	private BigDecimal cantidad;
	private BigDecimal preciounitario;
	private BigDecimal descuento;
	

}
