package com.erp.comercializacion.DTO;

import com.erp.comercializacion.models.Categorias;
import com.erp.comercializacion.models.Facturas;
import com.erp.comercializacion.models.Pliego24;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmisionOfCuentaDTO {
    Long cuenta;
    Long idfactura;
    int m3;
    int categoria;
    boolean swMunicipio;
    boolean swAdultoMayor;
    boolean swAguapotable;
    Pliego24 pliego24;
    Categorias categorias;
    Facturas factura;
}

