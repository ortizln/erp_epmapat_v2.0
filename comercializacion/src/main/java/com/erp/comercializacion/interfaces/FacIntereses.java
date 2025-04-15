package com.erp.comercializacion.interfaces;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public interface FacIntereses {
    Long getIdFactura();
    Float getSuma();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate getFeccrea();
    Long getFormapago();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate getFechatransferencia();
}
