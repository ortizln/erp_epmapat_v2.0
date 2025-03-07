package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ccertificaciones")
public class Ccertificaciones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idccertificacion;
    private Long numero;
    private Long anio;
    private String referenciadocumento;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtpcertifica_tpcertifica")
    private Tpcertifica idtpcertifica_tpcertifica;
    private Long idfactura_facturas;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente_clientes")
    private Clientes idcliente_clientes;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
}
