package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "liquidatrami")
public class Liquidatramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idliquidatrami;
    private Long cuota;
    private Float valor;
    private Long usuarioeliminacion;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechaeliminacion")
    private Date fechaeliminacion;
    private String razoneliminacion;
    private Long estado;
    private String observacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idtramite_tramites")
    private Ctramites idtramite_tramites;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idfactura_facturas")
    private Facturas idfactura_facturas;
    private Long usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
}
