package com.erp.sri.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name= "personeriajuridica")
public class PersonJuridica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idpjuridica;
    private String descripcion;
}
