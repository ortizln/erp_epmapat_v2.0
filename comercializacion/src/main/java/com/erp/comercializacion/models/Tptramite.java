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
@Table(name = "tptramite")
public class Tptramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtptramite;
    private String descripcion;
    private Long estado;
    private Long tipocalculo;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
}
