package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FacturasSinCobroInter {
    Long getIdfactura();
    BigDecimal getSubtotal();
    Long getFormapago();
    LocalDate getFeccrea();
    LocalDate getFechatransferencia();
    String getNombre();
}
