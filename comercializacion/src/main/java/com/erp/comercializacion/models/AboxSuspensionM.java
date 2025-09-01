package com.erp.comercializacion.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "aboxsuspension")
public class AboxSuspensionM {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idaboxsuspen;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idsuspension_suspensiones")
	private SuspensionesM idsuspension_suspensiones;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idabonado_abonados")
	private Abonados idabonado_abonados;

	
}
