package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface CarteraVencidaFacturas {
    Long getFactura();
    String getNombre();
    String getModulo();
    BigDecimal getTotal();
    Long getCuenta();
    Long getEmision();
    Long getM3();
}
