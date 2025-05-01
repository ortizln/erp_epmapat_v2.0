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
@Table(name = "facturamodificaciones")
public class Facturamodificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacturamodificaciones;
    private Long idfactura;
    private String datosfactura;
    private String detalle;
    private LocalDate fechacrea;
}
