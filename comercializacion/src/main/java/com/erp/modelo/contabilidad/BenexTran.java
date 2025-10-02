package com.erp.modelo.contabilidad;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.erp.modelo.administracion.Documentos;

@Entity
@Table(name = "benextran")
public class BenexTran {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idbenxtra; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inttra")
	private Transaci inttra; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intdoc")
	private Documentos intdoc; 
	private String numdoc; 
	private BigDecimal valor; 
	private BigDecimal totpagcob; 
	private BigDecimal pagocobro; 
	private Long idpagcob; 
	private Long intpre; 
	private String codparreci; 
	private String codcuereci; 
	private Long asierefe;
	
	public BenexTran() {
		super();
		
	}
	
	public Long getIdbenxtra() {
		return idbenxtra;
	}
	public void setIdbenxtra(Long idbenxtra) {
		this.idbenxtra = idbenxtra;
	}
	public Transaci getInttra() {
		return inttra;
	}
	public void setInttra(Transaci inttra) {
		this.inttra = inttra;
	}
	public Beneficiarios getIdbene() {
		return idbene;
	}
	public void setIdbene(Beneficiarios idbene) {
		this.idbene = idbene;
	}
	public Documentos getIntdoc() {
		return intdoc;
	}
	public void setIntdoc(Documentos intdoc) {
		this.intdoc = intdoc;
	}
	public String getNumdoc() {
		return numdoc;
	}
	public void setNumdoc(String numdoc) {
		this.numdoc = numdoc;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getTotpagcob() {
		return totpagcob;
	}
	public void setTotpagcob(BigDecimal totpagcob) {
		this.totpagcob = totpagcob;
	}
	public BigDecimal getPagocobro() {
		return pagocobro;
	}
	public void setPagocobro(BigDecimal pagocobro) {
		this.pagocobro = pagocobro;
	}
	public Long getIdpagcob() {
		return idpagcob;
	}
	public void setIdpagcob(Long idpagcob) {
		this.idpagcob = idpagcob;
	}
	public Long getIntpre() {
		return intpre;
	}
	public void setIntpre(Long intpre) {
		this.intpre = intpre;
	}
	public String getCodparreci() {
		return codparreci;
	}
	public void setCodparreci(String codparreci) {
		this.codparreci = codparreci;
	}
	public String getCodcuereci() {
		return codcuereci;
	}
	public void setCodcuereci(String codcuereci) {
		this.codcuereci = codcuereci;
	}
	public Long getAsierefe() {
		return asierefe;
	}
	public void setAsierefe(Long asierefe) {
		this.asierefe = asierefe;
	}

}
