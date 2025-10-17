package com.erp.interfaces;

import java.math.BigDecimal;

public interface RutasInterfaces {
    Long  getIdruta();
    String getRuta();
    Long getCuenta();
    String getNombre();
    String getCedula();
    String getDireccionubicacion();
    BigDecimal getSubtotal();
    BigDecimal getTotal_interes();
    BigDecimal getTotal_final();
    Long getTotal_facturas();
}
