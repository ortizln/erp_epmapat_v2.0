package com.erp.modelo.contabilidad;

import jakarta.persistence.*;

@Entity
@Table(name = "niveles")
public class Niveles {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idnivel; 
	private String nomniv; 
	private Integer nivcue;

	public Niveles(Long idnivel, String nomniv, Integer nivcue) {
		super();
		this.idnivel = idnivel;
		this.nomniv = nomniv;
		this.nivcue = nivcue;
	}

	public Niveles() {
		super();
		
	}

	public Long getIdnivel() {
		return idnivel;
	}
	public void setIdnivel(Long idnivel) {
		this.idnivel = idnivel;
	}

	public String getNomniv() {
		return nomniv;
	}
	public void setNomniv(String nomniv) {
		this.nomniv = nomniv;
	}
	public Integer getNivcue() {
		return nivcue;
	}
	public void setNivcue(Integer nivcue) {
		this.nivcue = nivcue;
	} 

}
