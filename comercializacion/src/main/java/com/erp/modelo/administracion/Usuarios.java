package com.erp.modelo.administracion;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")

public class Usuarios {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idusuario;
   private String identificausu;
   private String codusu;
   private String nomusu;
   private String email;
   private Boolean estado;
   @Column(name = "fdesde")
   private LocalDate fdesde;
   @Column(name = "fhasta")
   private LocalDate fhasta;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;
   private Long usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;
   private Boolean otrapestania;
   private String alias;
   private String priusu;
   private String perfil;
   private Long toolbarframe; 
   private Long toolbarsheet;

   // ====== Getteres y Setters =======
   public String getPerfil() {
      return perfil;
   }

   public void setPerfil(String perfil) {
      this.perfil = perfil;
   }

   public String getPriusu() {
      return priusu;
   }

   public void setPriusu(String priusu) {
      this.priusu = priusu;
   }

   public LocalDate getFdesde() {
      return fdesde;
   }

   public void setFdesde(LocalDate fdesde) {
      this.fdesde = fdesde;
   }

   public LocalDate getFhasta() {
      return fhasta;
   }

   public void setFhasta(LocalDate fhasta) {
      this.fhasta = fhasta;
   }

   public Long getIdusuario() {
      return idusuario;
   }

   public void setIdusuario(Long idusuario) {
      this.idusuario = idusuario;
   }

   public String getIdentificausu() {
      return identificausu;
   }

   public void setIdentificausu(String identificausu) {
      this.identificausu = identificausu;
   }

   public String getCodusu() {
      return codusu;
   }

   public void setCodusu(String codusu) {
      this.codusu = codusu;
   }

   public String getNomusu() {
      return nomusu;
   }

   public void setNomusu(String nomusu) {
      this.nomusu = nomusu;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public Boolean getEstado() {
      return estado;
   }

   public void setEstado(Boolean estado) {
      this.estado = estado;
   }

   public ZonedDateTime getFeccrea() {
      return feccrea;
   }

   public void setFeccrea(ZonedDateTime feccrea) {
      this.feccrea = feccrea;
   }

   public Long getUsumodi() {
      return usumodi;
   }

   public void setUsumodi(Long usumodi) {
      this.usumodi = usumodi;
   }

   public ZonedDateTime getFecmodi() {
      return fecmodi;
   }

   public void setFecmodi(ZonedDateTime fecmodi) {
      this.fecmodi = fecmodi;
   }

   public Boolean getOtrapestania() {
      return otrapestania;
   }

   public void setOtrapestania(Boolean otrapestania) {
      this.otrapestania = otrapestania;
   }

   public String getAlias() {
      return alias;
   }

   public void setAlias(String alias) {
      this.alias = alias;
   }

   public Long getToolbarframe() {
      return toolbarframe;
   }

   public void setToolbarframe(Long toolbarframe) {
      this.toolbarframe = toolbarframe;
   }

   public Long getToolbarsheet() {
      return toolbarsheet;
   }

   public void setToolbarsheet(Long toolbarsheet) {
      this.toolbarsheet = toolbarsheet;
   }
   

}