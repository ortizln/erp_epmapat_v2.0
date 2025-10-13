package com.erp.modelo.administracion;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
   private Long permissions;
}
