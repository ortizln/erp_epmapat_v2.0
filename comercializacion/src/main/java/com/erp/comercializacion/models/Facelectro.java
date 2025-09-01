package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "facelectro")

public class Facelectro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacelectro;
    private String codigonumerico;
    private String digitoverificador;
    private String nombrexml;
    private Integer estado;
    private String claveacceso;
    private Integer swenviada;
    private String identificacion;
    private String nombre;
    private String direccion;
    private String telefono;
    private String concepto;
    private Float base0;
    private Float baseimponiva;
    private Float iva12;
    private Float iva0;
    private Float total;
    private String numautorizacion;
    private String mensaje;
    private String tpidentifica;
    private Float impuesto;
    private Float impuestoretener;
    private Float porciva;
    private Long usucrea;
    private Date feccrea;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura_facturas")
    private Facturas idfactura_facturas;
    private Long idcaja_cajas;
    private String nrofac;

}
