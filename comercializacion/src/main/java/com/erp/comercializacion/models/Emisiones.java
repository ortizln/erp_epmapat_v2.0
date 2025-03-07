package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
