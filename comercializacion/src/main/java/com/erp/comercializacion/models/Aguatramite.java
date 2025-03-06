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
@Table(name="aguatramite")
public class Aguatramite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaguatramite;
    private String codmedidor;
    private String comentario;
    private Integer estado;
    private String sistema;
    private LocalDate fechaterminacion;
    private String observacion;
    private Long idfactura_facturas; //No todos los trámites tienen factura
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente_clientes")
    private Clientes idcliente_clientes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtipotramite_tipotramite")
    private Tipotramite idtipotramite_tipotramite;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;

}
