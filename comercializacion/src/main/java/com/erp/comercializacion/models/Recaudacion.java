package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recaudacion")
public class Recaudacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrecaudacion;
    @Column(name = "fechacobro")
    private ZonedDateTime fechacobro;
    private Long recaudador;
    private BigDecimal totalpagar;
    private BigDecimal recibo;
    private BigDecimal cambio;
    private Long formapago;
    private BigDecimal valor;
    private Integer estado;
    private Date fechaeliminacion;
    private Long usuarioeliminacion;
    private String observaciones;
    private Long ncnumero;
    private BigDecimal ncvalor;
    private Long usucrea;
    @Column(name = "feccrea")
    private ZonedDateTime feccrea;
}
