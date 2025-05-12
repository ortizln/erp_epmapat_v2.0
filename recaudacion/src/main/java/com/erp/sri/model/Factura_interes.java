package com.erp.sri.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Factura_interes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long idfactura;

    Long idmodulo;

    BigDecimal total;

    Long idcliente;

    Long idabonado;

    LocalDate feccrea;

    Long formapago;

    Long estado;

    Long pagado;

    Boolean swcondonar;

    BigDecimal interesacobrar;
    LocalDate fechacobro;
    Long usuariocobro;
    String nrofactura;
    BigDecimal swiva;
    String nombre;
}
