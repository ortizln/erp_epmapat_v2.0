package com.erp.modelo;

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
@Table(name = "catalogoitems")

public class Catalogoitems {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idcatalogoitems;
   private String descripcion;
   private Float cantidad;
   private Integer facturable;
   private Boolean estado;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idusoitems_usoitems")
	private Usoitems idusoitems_usoitems;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idrubro_rubros")
	private Rubros idrubro_rubros;
   private Long usucrea;
   private Date feccrea;
   private Long usumodi;
   private Date fecmodi;

}
