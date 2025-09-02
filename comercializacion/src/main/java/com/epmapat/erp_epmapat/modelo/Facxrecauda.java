package com.epmapat.erp_epmapat.modelo;

import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "facxrecauda")

public class Facxrecauda {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idfacxrecauda;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idrecaudacion")
   private Recaudacion idrecaudacion;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idfactura")
   private Facturas idfactura;

   private Integer estado;
   @Column(name = "fechaeliminacion")
   private ZonedDateTime fechaeliminacion;
   private Long usuarioeliminacion;

   public Long getIdfacxrecauda() {
      return idfacxrecauda;
   }
   public void setIdfacxrecauda(Long idfacxrecauda) {
      this.idfacxrecauda = idfacxrecauda;
   }

   public Recaudacion getIdrecaudacion() {
      return idrecaudacion;
   }
   public void setIdrecaudacion(Recaudacion idrecaudacion) {
      this.idrecaudacion = idrecaudacion;
   }

   public Facturas getIdfactura() {
      return idfactura;
   }
   public void setIdfactura(Facturas idfactura) {
      this.idfactura = idfactura;
   }

   public Integer getEstado() {
      return estado;
   }
   public void setEstado(Integer estado) {
      this.estado = estado;
   }
   
   public ZonedDateTime getFechaeliminacion() {
      return fechaeliminacion;
   }
   public void setFechaeliminacion(ZonedDateTime fechaeliminacion) {
      this.fechaeliminacion = fechaeliminacion;
   }
   
   public Long getUsuarioeliminacion() {
      return usuarioeliminacion;
   }
   public void setUsuarioeliminacion(Long usuarioeliminacion) {
      this.usuarioeliminacion = usuarioeliminacion;
   }
   
}
