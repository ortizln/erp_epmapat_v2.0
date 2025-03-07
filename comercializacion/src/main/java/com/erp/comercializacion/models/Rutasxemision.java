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
@Table(name = "rutasxemision")
public class Rutasxemision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrutaxemision;
    private Integer estado;
    private Long usuariocierre;
    private LocalDate fechacierre;
    private Long usucrea;
    private LocalDate feccrea;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision_emisiones")
    private Emisiones idemision_emisiones;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idruta_rutas")
    private Rutas idruta_rutas;
    private Long m3;
    private BigDecimal total;
}
