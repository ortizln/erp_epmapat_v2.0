package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "lecturas")
public class Lecturas implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idlectura;
   Integer estado;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idrutaxemision_rutasxemision")
   private Rutasxemision idrutaxemision_rutasxemision;
   Date fechaemision;
   Float lecturaanterior;
   Float lecturaactual;
   Float lecturadigitada;
   Integer mesesmulta;
   String observaciones;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idnovedad_novedades")
   private Novedad idnovedad_novedades;
   Long idemision;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idabonado_abonados")
   private Abonados idabonado_abonados;
   Long idresponsable;
   Long idcategoria;
   Long idfactura;
   private BigDecimal total1;
   private BigDecimal total31;
   private BigDecimal total32;


}