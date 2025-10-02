package com.erp.modelo.contabilidad;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "partixcerti")

public class Partixcerti {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idparxcer;
	private String descripcion;
	private BigDecimal valor;
	private BigDecimal saldo;
	private BigDecimal totprmisos;
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
	private Long inteje;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intpre")
	private Presupue intpre;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcerti")
	private Certipresu idcerti;
	private Long idparxcer_;

	public Partixcerti() {
		super();
	}

	public Long getIdparxcer() {
		return idparxcer;
	}
	public void setIdparxcer(Long idparxcer) {
		this.idparxcer = idparxcer;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public BigDecimal getTotprmisos() {
		return totprmisos;
	}
	public void setTotprmisos(BigDecimal totprmisos) {
		this.totprmisos = totprmisos;
	}
	public Long getUsucrea() {
		return usucrea;
	}
	public void setUsucrea(Long usucrea) {
		this.usucrea = usucrea;
	}
	public Date getFeccrea() {
		return feccrea;
	}
	public void setFeccrea(Date feccrea) {
		this.feccrea = feccrea;
	}
	public Long getUsumodi() {
		return usumodi;
	}
	public void setUsumodi(Long usumodi) {
		this.usumodi = usumodi;
	}
	public Date getFecmodi() {
		return fecmodi;
	}
	public void setFecmodi(Date fecmodi) {
		this.fecmodi = fecmodi;
	}
	public Long getInteje() {
		return inteje;
	}
	public void setInteje(Long inteje) {
		this.inteje = inteje;
	}
	public Presupue getIntpre() {
		return intpre;
	}
	public void setIntpre(Presupue intpre) {
		this.intpre = intpre;
	}
	public Certipresu getIdcerti() {
		return idcerti;
	}
	public void setIdcerti(Certipresu idcerti) {
		this.idcerti = idcerti;
	}
	public Long getIdparxcer_() {
		return idparxcer_;
	}
	public void setIdparxcer_(Long idparxcer_) {
		this.idparxcer_ = idparxcer_;
	}
	
}
