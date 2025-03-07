package com.erp.comercializacion.models;

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
@Table(name = "emisionindividual")
public class Emisionindividual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idemisionindividual;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision")
    private Emisiones idemision;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idlecturanueva")
    private Lecturas idlecturanueva;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idlecturaanterior")
    private Lecturas idlecturaanterior;
}
