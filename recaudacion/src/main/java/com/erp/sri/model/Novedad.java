package com.erp.sri.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "novedades")
public class Novedad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnovedad;
    private String descripcion;
    private Integer estado;
    private Long usucrea;
}
