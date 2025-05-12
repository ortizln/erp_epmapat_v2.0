package com.erp.sri.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
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
	private LocalDate feccrea;
	private Long usumodi;
	private LocalDate fecmodi;
}
