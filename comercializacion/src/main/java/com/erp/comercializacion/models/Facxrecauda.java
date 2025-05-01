package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facxrecauda")
public class Facxrecauda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacxrecauda;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrecaudacion")
    private Recaudacion idrecaudacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura")
    private Facturas idfactura;

    private Integer estado;
    @Column(name = "fechaeliminacion")
    private ZonedDateTime fechaeliminacion;
    private Long usuarioeliminacion;
}
