package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RepRefacturacion {
    Long getCuenta();
    String getNombre();
    Long getNuevaplanilla();
    BigDecimal getValornuevo();
    Long getAnteriorplanilla();
    BigDecimal  getValoranterior();
    String getObservaciones();
    LocalDate getFecelimina();
}
