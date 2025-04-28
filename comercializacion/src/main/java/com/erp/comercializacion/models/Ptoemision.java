package com.erp.comercializacion.models;

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
@Table(name = "ptoemision")
public class Ptoemision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idptoemision;
    private String establecimiento;
    private String direccion;
    private Long estado;
    private String telefono;
    private String nroautorizacion;
    private Long usucrea;
    private LocalDate feccrea;
}
