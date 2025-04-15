package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface RepFacEliminadas {
    Long getIdfactura();

    String getNomusu();

    String getRazoneliminacion();

    String getModulo();

    BigDecimal getTotal();
}
