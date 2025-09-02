package com.erp.interfaces;

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
}
