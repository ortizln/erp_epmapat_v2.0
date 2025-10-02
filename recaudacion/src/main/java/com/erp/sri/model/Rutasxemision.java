package com.erp.sri.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "rutasxemision")
public class Rutasxemision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrutaxemision;
    Integer estado;
    Long usuariocierre;
    LocalDate fechacierre;
    Long usucrea;
    LocalDate feccrea;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision_emisiones")
    private Emisiones idemision_emisiones;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idruta_rutas")
    private Rutas idruta_rutas;
    private Long m3;
    private BigDecimal total;
}
