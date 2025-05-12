package com.erp.sri.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "rutas")
public class Rutas {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idruta;
	private String descripcion;
	private Long orden;
	private String codigo;
	private Long usucrea;
	private LocalDate feccrea;
	private Long usumodi;
	private LocalDate fecmodi;
}
