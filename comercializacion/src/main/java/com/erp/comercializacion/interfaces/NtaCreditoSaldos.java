package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface NtaCreditoSaldos {
    Long getIdntacredito();
    Long getCuenta();
    BigDecimal getSaldo();
    BigDecimal getDevengado();
}
