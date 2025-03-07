package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "novedades")
public class Novedades {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnovedad;
    private String descripcion;
    private Integer estado;
    private Long usucrea;
}
