package com.erp.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "rutasxemision")
public class Rutasxemision implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idrutaxemision;
   Integer estado;
   Long usuariocierre;
   Date fechacierre;
   Long usucrea;
   Date feccrea;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idemision_emisiones")
   private Emisiones idemision_emisiones;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idruta_rutas")
   private Rutas idruta_rutas;
   private Long m3;
   private BigDecimal total;

}
