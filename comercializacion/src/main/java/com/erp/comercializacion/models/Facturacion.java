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
@Table(name = "facturacion")
public class Facturacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfacturacion;
    Integer estado;
    String descripcion;
    Integer formapago;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente_clientes")
    private Clientes idcliente_clientes;
    Float total;
    Short cuotas;
    Long usucrea;
    LocalDate feccrea;
    Long usumodi;
    LocalDate fecmodi;
}
