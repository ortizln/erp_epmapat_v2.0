package com.erp.sri.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
public class FacturaDTO {
    private Long idfactura;
    private Long idmodulo;
    private BigDecimal total;
    private Long idcliente;
    private Long idabonado;
    private LocalDate feccrea;
    private Long formaPago;
    private Long estado;
    private Long pagado;
    private Boolean swcondonar;
    private LocalDate fechacobro;
    private Long usuariocobro;
    private String nrofactura;
    private String nombre;
    private BigDecimal iva;
    private BigDecimal interes;
    private String direccion;
    private String modulo;
}
