package com.erp.comercializacion.models;

import java.math.BigDecimal;
import java.util.Date;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categorias")
public class Categorias {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcategoria;
	private String codigo;
	private String descripcion;
	private Boolean habilitado;
	private BigDecimal fijoagua;
	private BigDecimal fijosanea;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;


}
