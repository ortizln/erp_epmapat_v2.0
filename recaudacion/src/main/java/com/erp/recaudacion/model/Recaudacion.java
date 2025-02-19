package com.erp.recaudacion.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="recaudacion")
public class Recaudacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrecaudacion;
    private LocalDate fechacobro;
    private Long recaudador;
    private BigDecimal totalpagar;
    private BigDecimal recibo;
    private BigDecimal cambio;
    private Long formapago;
    private BigDecimal valor;
    private Long estado;
    private LocalDate fechaeliminacion;
    private Long usuarioeliminacion;
    private String observaciones;
    private Long ncnumero;
    private BigDecimal ncvalor;
    private Long usucrea;
    private LocalDate feccrea;
}
