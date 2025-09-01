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
@Table(name="ctramites")
public class CtramitesM {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idctramite;
	private Long estado;
	private Float total;
	private String descripcion;
	private Long cuotas;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "validohasta")
	private Date validohasta; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idtptramite_tptramite")
	private TpTramiteM idtptramite_tptramite;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idcliente_clientes")
	private Clientes idcliente_clientes;
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
