package com.erp.rrhh.modelo;

import java.time.LocalDate;

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
@Table(name = "th_actions")
public class ThAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpersonal_personal", nullable = false)
    private Personal idpersonal_personal;

    private String tipoaccion;
    private String motivo;
    private String observacion;
    private LocalDate fecvigencia;
    private LocalDate feccrea;
    private Long usucrea;
    private LocalDate fecmodi;
    private Long usumodi;
    private Boolean estado;
}

