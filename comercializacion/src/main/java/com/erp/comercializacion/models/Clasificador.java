package com.erp.comercializacion.models;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "clasificador")

public class Clasificador implements Serializable {

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
