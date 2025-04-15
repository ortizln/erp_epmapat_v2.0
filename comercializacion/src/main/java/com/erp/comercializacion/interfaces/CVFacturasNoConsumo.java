package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface CVFacturasNoConsumo {
    Long getFactura();
    String getNombre();
    String getModulo();
    BigDecimal getTotal();
    Long getCuenta();
}
