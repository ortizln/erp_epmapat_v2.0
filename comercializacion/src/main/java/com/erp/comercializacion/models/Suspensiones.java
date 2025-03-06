package com.erp.comercializacion.models;

import jakarta.persistence.*;

import java.time.LocalDate;

public class Suspensiones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idsuspension;
    private Long tipo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddocumento_documentos")
    private Documentos iddocumento_documentos;
    private String numdoc;
    private String observa;
    private Long usucrea;

    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
    private Long total;
}
