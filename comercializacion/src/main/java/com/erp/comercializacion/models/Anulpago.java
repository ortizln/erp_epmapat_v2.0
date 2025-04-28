package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "anulpago")
public class Anulpago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idanulpago;
    private String cedula;
    private String nombre;
    private Long idpropietarioemision;
    private String nrofactura;
    private BigDecimal totaltarifa;
    private Long idmodulo;
    private Integer usuarioanulacion;
    @Column(name = "fechaanulacion")
    private ZonedDateTime fechaanulacion;
    private String horaanulacion;
    private String razonanulacion;
    private Integer usuariocobro;
    @Column(name = "fechacobro")
    private ZonedDateTime fechacobro;
    private String horacobro;
    private BigDecimal valor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura")
    private Facturas idfactura;
    private BigDecimal descuento;
    private BigDecimal exoneracion;
    private BigDecimal interes;
}
