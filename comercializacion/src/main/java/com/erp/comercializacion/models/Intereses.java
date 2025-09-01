package com.erp.comercializacion.models;

import java.math.BigDecimal;
import java.time.LocalDate;
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
@Table(name = "intereses")
public class Intereses {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idinteres;
	private Long anio;
	private Long mes;
	private BigDecimal porcentaje; 
	private Long usucrea;
	/* @Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)*/
	@Column(name = "feccrea") 
	private LocalDate feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;

}
