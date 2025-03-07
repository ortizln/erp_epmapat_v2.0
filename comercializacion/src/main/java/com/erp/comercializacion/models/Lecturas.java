package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lecturas")
public class Lecturas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idlectura;
    Integer estado;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrutaxemision_rutasxemision")
    private Rutasxemision idrutaxemision_rutasxemision;
    private LocalDate fechaemision;
    private Float lecturaanterior;
    private Float lecturaactual;
    private Float lecturadigitada;
    private Integer mesesmulta;
    private String observaciones;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idnovedad_novedades")
    private Novedades idnovedad_novedades;
    private Long idemision;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado_abonados")
    private Abonados idabonado_abonados;
    private Long idresponsable;
    private Long idcategoria;
    private Long idfactura;
    private BigDecimal total1;
    private BigDecimal total31;
    private BigDecimal total32;
}
