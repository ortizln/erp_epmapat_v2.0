package com.erp.DTO;

import com.erp.modelo.Categorias;
import com.erp.modelo.Facturas;
import com.erp.modelo.Pliego24;

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

