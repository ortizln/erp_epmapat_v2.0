package com.erp.comercializacion.interfaces;

import java.util.Date;

public interface FacturasI {
    Long getIdfactura();

    Long getIdmodulo();

    String getNrofactura();

    Date getFechacobro();

    Long getConveniopago();

    Long getIdabonado();

    Long getFormapago();

    Long getIdcliente();
}
