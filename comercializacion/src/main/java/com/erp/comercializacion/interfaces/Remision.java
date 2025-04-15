package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface Remision {
    Long getIdfactura();
    String getDescripcion();
    LocalDate getFeccrea();
    BigDecimal getTotal();
    BigDecimal getIntereses();
    String getNrofactura();
}
