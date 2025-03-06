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
@Table(name = "modulos")
public class Modulos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idmodulo;
    private String descripcion;
    private Long estado;
    private Long periodicidad;
    private Long conveniospago;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;

}
