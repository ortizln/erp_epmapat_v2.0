package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pliego24")
public class Pliego24 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpliego;
    private Integer desde;
    private Integer hasta;
    private BigDecimal agua;
    private BigDecimal saneamiento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcategoria")
    private Categorias idcategoria;
    private BigDecimal porc;
}
