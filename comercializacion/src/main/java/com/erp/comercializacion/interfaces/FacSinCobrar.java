package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;
import java.util.Date;

public interface FacSinCobrar {
    Long getIdfactura();

    Long getIdmodulo();

    BigDecimal getTotal();

    Long getIdCliente();

    Long getIdAbonado();

    Date getFeccrea();

    Long getFormaPago();

    Long getEstado();

    Long getPagado();

    Boolean getSwcondonar();
}
