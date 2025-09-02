package com.epmapat.erp_epmapat.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "facxconvenio")

public class Facxconvenio {
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idfacxconvenio;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idfactura_facturas")
	private Facturas idfactura_facturas;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idconvenio_convenios")
	private Convenios idconvenio_convenios;
	//private Long idfactura_facturas;
	//private Long idconvenio_convenios;
	
	public Facxconvenio() {
	}
	
	// public Facxconvenio(Long idfacxconvenio, Facturas idfactura_facturas, Convenios idconvenio_convenios) {
	// 	this.idfacxconvenio = idfacxconvenio;
	// 	this.idfactura_facturas = idfactura_facturas;
	// 	this.idconvenio_convenios = idconvenio_convenios;
	// }

	public Long getIdfacxconvenio() {
		return idfacxconvenio;
	}
	public void setIdfacxconvenio(Long idfacxconvenio) {
		this.idfacxconvenio = idfacxconvenio;
	}
	
	public Facturas getIdfactura_facturas() {
		return idfactura_facturas;
	}
	public void setIdfactura_facturas(Facturas idfactura_facturas) {
		this.idfactura_facturas = idfactura_facturas;
	}
	
	public Convenios getIdconvenio_convenios() {
		return idconvenio_convenios;
	}
	public void setIdconvenio_convenios(Convenios idconvenio_convenios) {
		this.idconvenio_convenios = idconvenio_convenios;
	}

}
