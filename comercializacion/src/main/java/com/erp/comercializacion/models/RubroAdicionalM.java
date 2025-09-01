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
@Table(name = "rubroadicional")

public class RubroAdicionalM {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idrubroadicional;
	private Float valor;
	private Long swiva;
	private Long rubroprincipal;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Column(name = "fecmodi")
	private Date fecmodi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idrubro_rubros")
	private Rubros idrubro_rubros;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtptramite_tptramite")
	private TpTramiteM idtptramite_tptramite;
	
}
