package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface RepEmisionindividual {
    Long getFacturaa();

    String getEmisiona();

    Long getFacturan();

    String getEmisionn();

    Long getCuenta();

    BigDecimal getTanterior();

    BigDecimal getTnuevo();
}
