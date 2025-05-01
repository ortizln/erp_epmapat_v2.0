package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "condmultasintereses")
public class Condmultasintereses {
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
    private LocalDate feccrea;
}
