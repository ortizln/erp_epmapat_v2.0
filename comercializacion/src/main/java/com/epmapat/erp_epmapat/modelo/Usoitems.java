package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "usoitems")

public class Usoitems {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idusoitems;
   private String descripcion;
   private Boolean estado;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idmodulo_modulos")
	private Modulos idmodulo_modulos;
   private Long usucrea;
   private Date feccrea;
   private Long usumodi;
   private Date fecmodi;

   public Long getIdusoitems() {
      return idusoitems;
   }

   public void setIdusoitems(Long idusoitems) {
      this.idusoitems = idusoitems;
   }

   public String getDescripcion() {
      return descripcion;
   }

   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }

   public Boolean getEstado() {
      return estado;
   }

   public void setEstado(Boolean estado) {
      this.estado = estado;
   }

   public Modulos getIdmodulo_modulos() {
      return idmodulo_modulos;
   }

   public void setIdmodulo_modulos(Modulos idmodulo_modulos) {
      this.idmodulo_modulos = idmodulo_modulos;
   }

   public Long getUsucrea() {
      return usucrea;
   }

   public void setUsucrea(Long usucrea) {
      this.usucrea = usucrea;
   }

   public Date getFeccrea() {
      return feccrea;
   }

   public void setFeccrea(Date feccrea) {
      this.feccrea = feccrea;
   }

   public Long getUsumodi() {
      return usumodi;
   }

   public void setUsumodi(Long usumodi) {
      this.usumodi = usumodi;
   }

   public Date getFecmodi() {
      return fecmodi;
   }

   public void setFecmodi(Date fecmodi) {
      this.fecmodi = fecmodi;
   }

}
