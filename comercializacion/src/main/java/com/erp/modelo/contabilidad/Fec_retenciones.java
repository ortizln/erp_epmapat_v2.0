package com.erp.modelo.contabilidad;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fec_retenciones")

public class Fec_retenciones implements Serializable {
   @Id
   private Long idretencion;
   private String claveacceso;
   private String secuencial;
   private String xmlautorizado;
   private String errores;
   private String estado;
   private String establecimiento;
   private String puntoemision;
   private String direccionestablecimiento;
   private LocalDate fechaemision;

   private String tipoidentificacionsujetoretenid;

   private String razonsocialsujetoretenido;
   private String identificacionsujetoretenido;
   private String periodofiscal;
   private String telefonosujetoretenido;
   private String emailsujetoretenido;

}
