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
@Table(name = "ingreso_documentos")
public class Ingreso_documentos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha_recepcion;
    private String codigo_documento;
    private String procedencia;
    private String asunto;
    private String departamento_receptor;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seccion_id")
    private Seccion seccion_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subseccion_id")
    private Subsecciones subseccion_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "serie_id")
    private Series serie_id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asubserie_id")
    private Subseries subserie_id;
    private Long numero_fojas;
    private String nombre_quien_firma;
    private String sumilla_a;
    private String estatus;
    private LocalDate fecha_revision;
    private String notas;
    private LocalDate fecha_documento;
    private Long archivo;
}
