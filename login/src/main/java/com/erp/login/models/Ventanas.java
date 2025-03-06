package com.erp.login.models;

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
@Table(name = "ventanas")
public class Ventanas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idventana;
    private String nombre;
    private String color1;
    private String color2;
    private Long idusuario;

}
