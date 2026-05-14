package com.erp.comercializacion.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "fec_factura")

public class Fec_factura implements Serializable {
   @Id
   private Long idfactura;
   private String claveacceso;
   private String secuencial;
   private String xmlautorizado;
   private String errores;
   private String estado;
   private String establecimiento;
   private String puntoemision;
   private String direccionestablecimiento;
   private LocalDateTime fechaemision;
   private String tipoidentificacioncomprador;
   private String guiaremision;
   private String razonsocialcomprador;
   private String identificacioncomprador;
   private String direccioncomprador;
   private String telefonocomprador;
   private String emailcomprador;
   private String concepto;
   private String referencia;
   private String recaudador;
   private Integer intentosAutorizacion;
   private LocalDateTime fechaUltimoIntento;
   private LocalDateTime fechaAutorizacion;
   private Boolean mailEnviado;

   public Long getIdfactura() {
      return idfactura;
   }

   public void setIdfactura(Long idfactura) {
      this.idfactura = idfactura;
   }

   public String getClaveacceso() {
      return claveacceso;
   }

   public void setClaveacceso(String claveacceso) {
      this.claveacceso = claveacceso;
   }

   public String getSecuencial() {
      return secuencial;
   }

   public void setSecuencial(String secuencial) {
      this.secuencial = secuencial;
   }

   public String getXmlautorizado() {
      return xmlautorizado;
   }

   public void setXmlautorizado(String xmlautorizado) {
      this.xmlautorizado = xmlautorizado;
   }

   public String getErrores() {
      return errores;
   }

   public void setErrores(String errores) {
      this.errores = errores;
   }

   public String getEstado() {
      return estado;
   }

   public void setEstado(String estado) {
      this.estado = estado;
   }

   public String getEstablecimiento() {
      return establecimiento;
   }

   public void setEstablecimiento(String establecimiento) {
      this.establecimiento = establecimiento;
   }

   public String getPuntoemision() {
      return puntoemision;
   }

   public void setPuntoemision(String puntoemision) {
      this.puntoemision = puntoemision;
   }

   public String getDireccionestablecimiento() {
      return direccionestablecimiento;
   }

   public void setDireccionestablecimiento(String direccionestablecimiento) {
      this.direccionestablecimiento = direccionestablecimiento;
   }

   public LocalDateTime getFechaemision() {
      return fechaemision;
   }

   public void setFechaemision(LocalDateTime fechaemision) {
      this.fechaemision = fechaemision;
   }

   public String getTipoidentificacioncomprador() {
      return tipoidentificacioncomprador;
   }

   public void setTipoidentificacioncomprador(String tipoidentificacioncomprador) {
      this.tipoidentificacioncomprador = tipoidentificacioncomprador;
   }

   public String getGuiaremision() {
      return guiaremision;
   }

   public void setGuiaremision(String guiaremision) {
      this.guiaremision = guiaremision;
   }

   public String getRazonsocialcomprador() {
      return razonsocialcomprador;
   }

   public void setRazonsocialcomprador(String razonsocialcomprador) {
      this.razonsocialcomprador = razonsocialcomprador;
   }

   public String getIdentificacioncomprador() {
      return identificacioncomprador;
   }

   public void setIdentificacioncomprador(String identificacioncomprador) {
      this.identificacioncomprador = identificacioncomprador;
   }

   public String getDireccioncomprador() {
      return direccioncomprador;
   }

   public void setDireccioncomprador(String direccioncomprador) {
      this.direccioncomprador = direccioncomprador;
   }

   public String getTelefonocomprador() {
      return telefonocomprador;
   }

   public void setTelefonocomprador(String telefonocomprador) {
      this.telefonocomprador = telefonocomprador;
   }

   public String getEmailcomprador() {
      return emailcomprador;
   }

   public void setEmailcomprador(String emailcomprador) {
      this.emailcomprador = emailcomprador;
   }

   public String getConcepto() {
      return concepto;
   }

   public void setConcepto(String concepto) {
      this.concepto = concepto;
   }

   public String getReferencia() {
      return referencia;
   }

   public void setReferencia(String referencia) {
      this.referencia = referencia;
   }

   public String getRecaudador() {
      return recaudador;
   }

   public void setRecaudador(String recaudador) {
      this.recaudador = recaudador;
   }

   public Integer getIntentosAutorizacion() {
      return intentosAutorizacion;
   }

   public void setIntentosAutorizacion(Integer intentosAutorizacion) {
      this.intentosAutorizacion = intentosAutorizacion;
   }

   public LocalDateTime getFechaUltimoIntento() {
      return fechaUltimoIntento;
   }

   public void setFechaUltimoIntento(LocalDateTime fechaUltimoIntento) {
      this.fechaUltimoIntento = fechaUltimoIntento;
   }

   public LocalDateTime getFechaAutorizacion() {
      return fechaAutorizacion;
   }

   public void setFechaAutorizacion(LocalDateTime fechaAutorizacion) {
      this.fechaAutorizacion = fechaAutorizacion;
   }

   public Boolean getMailEnviado() {
      return mailEnviado;
   }

   public void setMailEnviado(Boolean mailEnviado) {
      this.mailEnviado = mailEnviado;
   }

public Fec_factura orElseThrow(Object object) {
    
    throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
}

}

