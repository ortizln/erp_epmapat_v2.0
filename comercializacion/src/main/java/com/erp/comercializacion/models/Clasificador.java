package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "clasificador")
public class Clasificador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intcla;
    String codpar;
    Integer nivpar;
    String grupar;
    String nompar;
    String despar;
    String cueejepresu;
    Integer presupuesto;
    Integer ejecucion;
    Integer devengado;
    Integer reforma;
    Integer asigna_ini;
    String usucrea;
    Date feccrea;
    String usumodi;
    Date fecmodi;
    String grupo;
    Integer balancostos;
}
