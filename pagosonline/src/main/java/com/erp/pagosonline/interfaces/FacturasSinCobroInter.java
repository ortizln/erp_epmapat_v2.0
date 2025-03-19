package com.erp.pagosonline.interfaces;

import java.math.BigDecimal;

public interface FacturasSinCobroInter {
    Long getIdfactura();
    BigDecimal getValor();
    BigDecimal getInteres();
}
