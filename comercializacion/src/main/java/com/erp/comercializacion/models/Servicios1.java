package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="servicios1")
public class Servicios1 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idservicio;
    private String descripcion;
    private Long estado;
    private Long swconsumo;
    private Float valor;
    private Long swiva;
    private Long swinvent;
    private Long facturable;
    private String nombre;
    private Long usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
    private Long usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecmodi")
    private Date fecmodi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="idmodulo_modulos")
    private Modulos idmodulo_modulos;
    private Long tipocalculo;
    private Float serviadmi;
    private Float descuento;
    private Float otro;

}
