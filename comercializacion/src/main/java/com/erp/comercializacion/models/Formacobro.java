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
@Table(name = "formacobro")
public class Formacobro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idformacobro;
    private String descripcion;
    private boolean estado;
    private Long usumodi;
    private LocalDate fecmodi;
}
