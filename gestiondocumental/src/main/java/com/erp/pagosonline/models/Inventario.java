package com.erp.pagosonline.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inventario")
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caja_id")
    private Gdcajas caja_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Subseries serie_id;
    private String numero_expediente;
    private String descripcion;
    private LocalDate fecha_apertura;
    private LocalDate fecha_cierre;
    private Long numero_fojas;
    private String destino_final;
    private String soporte;
    private String zona;
    private String estanteria;
    private String bandeja;
    private String observaciones;
}
