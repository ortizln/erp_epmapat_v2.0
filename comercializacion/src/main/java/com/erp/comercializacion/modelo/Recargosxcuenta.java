package com.erp.comercializacion.modelo;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recargosxcuenta")
public class Recargosxcuenta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrecargoxcuenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado_abonados")
    private Abonados idabonado_abonados;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision_emisiones")
    private Emisiones idemision_emisiones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrubro_rubros")
    private Rubros idrubro_rubros;

    private int tipo;
    private String observacion;
    private Long usucrea;
    private Timestamp feccrea;
    private Long usumodi;
    private Timestamp fecmodi;
    private Long usuresp;
    private Timestamp fecha;
}

