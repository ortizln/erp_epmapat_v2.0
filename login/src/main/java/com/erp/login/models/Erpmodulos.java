package com.erp.login.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "erpmodulos")
public class Erpmodulos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long iderpmodulo;
    private String descripcion;
}
