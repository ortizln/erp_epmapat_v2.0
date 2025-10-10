package com.erp.modelo.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "fec_retenciones_impuestos")

public class Fec_reteimpu implements Serializable {
   @Id
   private Long idretencionesimpuestos;
   private Long idretencion;
   private String codigo;
   private String codigoporcentaje;
   private BigDecimal baseimponible;
   private String codigodocumentosustento;
   private String numerodocumentosustento;
   private Date fechaemisiondocumentosustento;
   


   
}
