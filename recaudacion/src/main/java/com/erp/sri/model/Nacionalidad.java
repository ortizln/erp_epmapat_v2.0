package com.erp.sri.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "nacionalidad")
public class Nacionalidad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnacionalidad;
    private String descripcion;    
}
