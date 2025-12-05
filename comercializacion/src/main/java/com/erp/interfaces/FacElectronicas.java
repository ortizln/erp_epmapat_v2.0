package com.erp.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FacElectronicas {
    Long getIdfactura();
    String getNrofactura();
    LocalDate getFechaemision();
    String getRazonsocialcomprador();
    String getEmailcomprador();
    String getEstado();
    String getXmlautorizado();
    Long getReferencia();
    String getDireccioncomprador();
    BigDecimal getTotal();
}
