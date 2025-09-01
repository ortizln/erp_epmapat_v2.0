package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "novedades")
public class Novedad implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnovedad;
    private String descripcion;
    Integer estado;
    Long usucrea;
    //Date feccrea;
    
    @Override
    public String toString() {
        return "Novedad [descripcion=" + descripcion + ", estado=" + estado + ", idnovedad=" + idnovedad + ", usucrea="
                + usucrea + "]";
    }

}
