package com.erp.comercializacion.models;

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
@Table(name = "ptoemision")
public class PtoEmisionM {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idptoemision;
	private String establecimiento;
	private String direccion;
	private Long estado;
	private String telefono;
	private String nroautorizacion;
	private Long usucrea;	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;

}
