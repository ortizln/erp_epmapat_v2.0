package com.erp.interfaces;

import java.time.LocalDate;
import java.math.BigDecimal;

public interface FacTransferencias {
    Long getIdfactura();

    BigDecimal getTotal();

    LocalDate getFechatransferencia();

    Long getIdmodulo();

    String getNombre();
    String getNrofactura();
}
