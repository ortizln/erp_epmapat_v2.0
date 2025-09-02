package com.erp.modelo.administracion;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "colores")
public class Colores {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idcolor;
   private String codigo;
   private String nombre;
   private String nombrecss;
   private String hexadecimal;
   private String rgb;

   public Long getIdcolor() {
      return idcolor;
   }
   public void setIdcolor(Long idcolor) {
      this.idcolor = idcolor;
   }
   public String getCodigo() {
      return codigo;
   }
   public void setCodigo(String codigo) {
      this.codigo = codigo;
   }
   public String getNombre() {
      return nombre;
   }
   public void setNombre(String nombre) {
      this.nombre = nombre;
   }
   public String getNombrecss() {
      return nombrecss;
   }
   public void setNombrecss(String nombrecss) {
      this.nombrecss = nombrecss;
   }
   public String getHexadecimal() {
      return hexadecimal;
   }
   public void setHexadecimal(String hexadecimal) {
      this.hexadecimal = hexadecimal;
   }
   public String getRgb() {
      return rgb;
   }
   public void setRgb(String rgb) {
      this.rgb = rgb;
   }


}

