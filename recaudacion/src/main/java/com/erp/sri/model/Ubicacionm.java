package com.erp.sri.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "abonados")
public class Ubicacionm {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idubicacionm;
	private String descripcion;
	private Long usucrea;
	private LocalDate feccrea;
	private Long usumodi;
	private LocalDate fecmodi;

}
