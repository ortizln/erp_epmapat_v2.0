package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface REcaudaFacturasI {
    Long getIdfactura();

    String getNombre();

    String getNrofactura();

    Long getEstado();

    Long getFormapago();

    BigDecimal getValor();

    LocalDate getFechacobro();

    String getNomusu();

    Long getIdabonado();
}
