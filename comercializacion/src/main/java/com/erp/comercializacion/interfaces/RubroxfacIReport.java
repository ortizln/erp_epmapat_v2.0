package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface RubroxfacIReport {
    Long getIdrubro_rubros();

    String getDescripcion();

    BigDecimal getTotal();

    Long getAbonados();

    Long getM3();
}
