package com.erp.sri.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Factura_int {
	Long getIdfactura();

	Long getIdmodulo();

	BigDecimal getTotal();

	Long getIdCliente();

	Long getIdAbonado();

	LocalDate getFeccrea();

	Long getFormaPago();

	Long getEstado();

	Long getPagado();

	Boolean getSwcondonar();

	LocalDate getfechacobro();
	Long getUsuariocobro();
	String getNrofactura();
	String getNombre();

}
