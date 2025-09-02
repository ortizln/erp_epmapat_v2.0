package com.epmapat.erp_epmapat.modelo.contabilidad;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "niifhomologa")

public class NiifHomologa {
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private Long idhomologa;
	private String codcueniif;
	private String codcue;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idniifcue")
	private NiifCuentas idniifcue;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcuenta")
	private Cuentas idcuenta;

	public NiifHomologa() {
		super();
		// TODO Auto-generated constructor stub
	}

	public NiifHomologa(Long idhomologa, String codcueniif, String codcue, NiifCuentas idniifcue, Cuentas idcuenta) {
		super();
		this.idhomologa = idhomologa;
		this.codcueniif = codcueniif;
		this.codcue = codcue;
		this.idniifcue = idniifcue;
		this.idcuenta = idcuenta;
	}

	public Long getIdhomologa() {
		return idhomologa;
	}

	public void setIdhomologa(Long idhomologa) {
		this.idhomologa = idhomologa;
	}

	public String getCodcueniif() {
		return codcueniif;
	}

	public void setCodcueniif(String codcueniif) {
		this.codcueniif = codcueniif;
	}

	public String getCodcue() {
		return codcue;
	}

	public void setCodcue(String codcue) {
		this.codcue = codcue;
	}

	public NiifCuentas getIdniifcue() {
		return idniifcue;
	}

	public void setIdniifcue(NiifCuentas idniifcue) {
		this.idniifcue = idniifcue;
	}

	public Cuentas getIdcuenta() {
		return idcuenta;
	}

	public void setIdcuenta(Cuentas idcuenta) {
		this.idcuenta = idcuenta;
	}

}
