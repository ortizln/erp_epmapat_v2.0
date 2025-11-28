package com.erp.epmapaApi.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FacturasSinCobroInter {
    Long getIdfactura();

    Float getSubtotal();

    String getNombre();

    String getCedula();

    String getDireccionubicacion();

    Long getCuenta();

    LocalDate getFectransferencia();

    Long getFormapago();

    LocalDate getFeccrea();

    BigDecimal getIntereses();

    BigDecimal getTotal();

    Long getNum_facturas();
}
