package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface RepFacEliminadasByEmision {
    Long getPlanilla();

    Long getIdlectura();

    Long getEmision();

    Long getCuenta();

    String getNombre();

    String getRuta();

    BigDecimal getSuma();
}
