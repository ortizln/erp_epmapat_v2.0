package com.erp.modelo.administracion;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.persistence.Entity;

@Entity
@Table(name = "acceso")

public class Acceso {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idacc;
   private String codacc;
   private String nomacc;
   private Integer regacc;

   public Long getIdacc() {
      return idacc;
   }
   public void setIdacc(Long idacc) {
      this.idacc = idacc;
   }
   public String getCodacc() {
      return codacc;
   }
   public void setCodacc(String codacc) {
      this.codacc = codacc;
   }
   public String getNomacc() {
      return nomacc;
   }
   public void setNomacc(String nomacc) {
      this.nomacc = nomacc;
   }
   public Integer getRegacc() {
      return regacc;
   }
   public void setRegacc(Integer regacc) {
      this.regacc = regacc;
   }
   
}
