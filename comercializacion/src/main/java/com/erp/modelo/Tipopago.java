package com.erp.modelo;

import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tipopago")
public class Tipopago implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtipopago;
    String descripcion;
    Long usucrea;

    public Tipopago() {
    }

    public Tipopago(Long idtipopago, String descripcion, Long usucrea) {
        this.idtipopago = idtipopago;
        this.descripcion = descripcion;
        this.usucrea = usucrea;
    }

    public Long getIdtipopago() {
        return idtipopago;
    }

    public void setIdtipopago(Long idtipopago) {
        this.idtipopago = idtipopago;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Long getUsucrea() {
        return usucrea;
    }

    public void setUsucrea(Long usucrea) {
        this.usucrea = usucrea;
    }

    @Override
    public String toString() {
        return "Tipopago [descripcion=" + descripcion + ", idtipopago=" + idtipopago + ", usucrea=" + usucrea + "]";
    }

}
