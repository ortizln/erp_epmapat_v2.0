package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FacTransferencias {
    Long getIdfactura();

    BigDecimal getTotal();

    LocalDate getFechatransferencia();

    Long getIdmodulo();

    String getNombre();
}
