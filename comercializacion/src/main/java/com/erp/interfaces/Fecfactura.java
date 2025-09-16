package com.erp.interfaces;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface Fecfactura {
    Long getIdfactura();
    String getNrofactura();
    LocalDate getFechacobro(); // si la columna es TIMESTAMP
    String getNomusu();
    String getIdabonado();
    String getNombre();
    String getCedula();
    String getEmail();
    String getTelefono();
    String getCodigo();
    String getDireccion();
}

