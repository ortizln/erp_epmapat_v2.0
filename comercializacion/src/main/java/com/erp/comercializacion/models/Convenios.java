package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="convenios")
public class Convenios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idconvenio;
    private String nroautorizacion;
    private String referencia;
    private Integer estado;
    private Integer nroconvenio;
    private BigDecimal totalconvenio;
    private Short cuotas;
    private BigDecimal cuotainicial;
    private BigDecimal pagomensual;
    private BigDecimal cuotafinal;
    private String observaciones;
    private Long usuarioeliminacion;
    private LocalDate fechaeliminacion;
    private String razoneliminacion;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private Timestamp fecmodi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado")
    private Abonados idabonado;
}
