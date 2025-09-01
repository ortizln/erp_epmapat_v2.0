package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "condmultasintereses")
public class CondMultasIntereses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcondmultainteres;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura_facturas")
    private Facturas idfactura_facturas;
    private BigDecimal totalinteres;
    private BigDecimal totalmultas;
    private String razoncondonacion;
    private Long usucrea;
    private Date feccrea;
    public Long getIdcondmultainteres() {
        return idcondmultainteres;
    }
    public void setIdcondmultainteres(Long idcondmultainteres) {
        this.idcondmultainteres = idcondmultainteres;
    }
    public Facturas getIdfactura_facturas() {
        return idfactura_facturas;
    }
    public void setIdfactura_facturas(Facturas idfactura_facturas) {
        this.idfactura_facturas = idfactura_facturas;
    }
    public BigDecimal getTotalinteres() {
        return totalinteres;
    }
    public void setTotalinteres(BigDecimal totalinteres) {
        this.totalinteres = totalinteres;
    }
    public BigDecimal getTotalmultas() {
        return totalmultas;
    }
    public void setTotalmultas(BigDecimal totalmultas) {
        this.totalmultas = totalmultas;
    }
    public String getRazoncondonacion() {
        return razoncondonacion;
    }
    public void setRazoncondonacion(String razoncondonacion) {
        this.razoncondonacion = razoncondonacion;
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
    

}
