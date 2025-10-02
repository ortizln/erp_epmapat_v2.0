package com.erp.modelo;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "formacobro")

public class Formacobro {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idformacobro;
   String descripcion;
   boolean estado;
   Long usumodi;
   Date fecmodi;

   public Long getIdformacobro() {
      return idformacobro;
   }
   public void setIdformacobro(Long idformacobro) {
      this.idformacobro = idformacobro;
   }
   public String getDescripcion() {
      return descripcion;
   }
   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }
   public boolean isEstado() {
      return estado;
   }
   public void setEstado(boolean estado) {
      this.estado = estado;
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
