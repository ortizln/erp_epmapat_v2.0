package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ctramites")
public class Ctramites {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idctramite;
    private Long estado;
    private Float total;
    private String descripcion;
    private Long cuotas;
    private LocalDate validohasta;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idtptramite_tptramite")
    private Tptramite idtptramite_tptramite;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idcliente_clientes")
    private Clientes idcliente_clientes;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
}
