package com.erp.pagosonline.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public interface FacturasCobradas {
    int getNrubros();
    BigDecimal getValortotal();
    Long getPlanilla();
    String getNrofactura();
    LocalDate getFechacobro();
    LocalTime getHoracobro();
    String getUsuario();
}
