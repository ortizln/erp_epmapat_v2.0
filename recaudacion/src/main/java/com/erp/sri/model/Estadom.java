package com.erp.sri.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "estadom")
public class Estadom {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idestadom;
	private String descripcion;
	private Long usucrea;
	private LocalDate feccrea;
	private Long usumodi;
	private LocalDate fecmodi;
}
