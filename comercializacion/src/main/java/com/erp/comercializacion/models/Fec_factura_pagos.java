package com.erp.comercializacion.models;

import jakarta.persistence.*;
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
	public Long getIdfacturapagos() {
		return idfacturapagos;
	}
	public void setIdfacturapagos(Long idfacturapagos) {
		this.idfacturapagos = idfacturapagos;
	}
	public Long getIdfactura() {
		return idfactura;
	}
	public void setIdfactura(Long idfactura) {
		this.idfactura = idfactura;
	}
	public String getFormapago() {
		return formapago;
	}
	public void setFormapago(String formapago) {
		this.formapago = formapago;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Integer getPlazo() {
		return plazo;
	}
	public void setPlazo(Integer plazo) {
		this.plazo = plazo;
	}
	public String getUnidadtiempo() {
		return unidadtiempo;
	}
	public void setUnidadtiempo(String unidadtiempo) {
		this.unidadtiempo = unidadtiempo;
	}

}
