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
@Table(name= "cajas")
public class Cajas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcaja;
	private String descripcion;
	private String codigo;
	private Long estado;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idptoemision_ptoemision")
	private PtoEmisionM idptoemision_ptoemision;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name="feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name="fecmodi")
	private Date fecmodi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idusuario_usuarios")
	private Usuarios idusuario_usuarios; 
	private String ultimafact; 
	
	public String getUltimafact() {
		return ultimafact;
	}
	public void setUltimafact(String ultimafact) {
		this.ultimafact = ultimafact;
	}
	public Long getIdcaja() {
		return idcaja;
	}
	public void setIdcaja(Long idcaja) {
		this.idcaja = idcaja;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
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

}
