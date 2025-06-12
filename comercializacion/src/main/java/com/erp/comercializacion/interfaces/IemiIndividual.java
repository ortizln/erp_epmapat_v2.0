package com.erp.comercializacion.interfaces;

import java.math.BigDecimal;

public interface IemiIndividual{
    Long getRubro();

    String getDescripcion();

    Long getNroFacturas();

    BigDecimal getSumaTotal();

}
