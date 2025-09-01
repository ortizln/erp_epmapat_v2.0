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
@Table(name = "ccertificaciones")
public class Ccertificaciones {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idccertificacion;
	private Long numero;
	private Long anio;
	private String referenciadocumento;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtpcertifica_tpcertifica")
	private TpCertifica idtpcertifica_tpcertifica;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idfactura_facturas")
	private Facturas idfactura_facturas;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente_clientes")
	private Clientes idcliente_clientes; 
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name ="fecmodi")
	private Date fecmodi;


}
