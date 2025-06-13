package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recaudaxcaja")
public class Recaudaxcaja {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrecaudaxcaja;
    private Integer estado;
    private Long facinicio;
    private Long facfin;
    private Date fechainiciolabor;
    private Date fechafinlabor;
    private LocalTime horainicio;
    private LocalTime horafin;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcaja_cajas")
    private Cajas idcaja_cajas;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario_usuarios")
    private Usuarios idusuario_usuarios;
}
