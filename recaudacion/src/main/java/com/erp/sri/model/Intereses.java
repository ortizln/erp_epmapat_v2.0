package com.erp.sri.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "intereses")
public class Intereses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idinteres;
    private Long anio;
    private Long mes;
    private BigDecimal porcentaje;
    private Long usucrea;
    @Column(name = "feccrea")
    private LocalDate feccrea;
    private Long usumodi;
    @Column(name = "fecmodi")
    private LocalDate fecmodi;
}
