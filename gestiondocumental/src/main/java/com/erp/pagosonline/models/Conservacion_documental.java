package com.erp.pagosonline.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conservacion_documental")
public class Conservacion_documental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subserie_id")
    private Subseries subserie_id;
    private Long plazo_gestion;
    private Long plazo_central;
    private Long plazo_intermedio;
    private Long plazo_historico;
    private String base_legal;
    private String disposision_final;
    private String tecnica_seleccion;
}
