package com.erp.sri.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "emisiones")
public class Emisiones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idemision;
    String emision;
    Integer estado;
    String observaciones;
    Long usuariocierre;
    @Column(name = "fechacierre")
    private ZonedDateTime fechacierre;
    Long m3;
    Long usucrea;
    LocalDate feccrea;
    Long usumodi;
    LocalDate fecmodi;
}
