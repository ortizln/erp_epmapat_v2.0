package com.erp.sri.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Factura_int {
	Long getIdfactura();

	Long getIdmodulo();

	BigDecimal getTotal();

	Long getIdcliente();

	Long getIdabonado();

	LocalDate getFeccrea();

	Long getFormaPago();

	Long getEstado();

	Long getPagado();

	Boolean getSwcondonar();

	LocalDate getfechacobro();
	Long getUsuariocobro();
	String getNrofactura();
	String getNombre();
	BigDecimal getIva();
	BigDecimal getInteres();
	String getDireccion();
    String getModulo();
}
