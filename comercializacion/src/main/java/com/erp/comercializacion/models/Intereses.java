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
@Table(name="intereses")
public class Intereses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idinteres;
    private Long anio;
    private Long mes;
    private BigDecimal porcentaje;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;

}
