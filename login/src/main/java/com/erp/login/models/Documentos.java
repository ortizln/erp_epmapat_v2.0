package com.erp.login.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documentos")
public class Documentos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long intdoc;
    private String nomdoc;
    private Long tipdoc;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtabla4")
    private Tabla4 idtabla4;
    private String tipocomprobante;
    private Long usucrea;
    @Column(name = "feccrea")
    private ZonedDateTime feccrea;
    private Long usumodi;
    @Column(name = "fecmodi")
    private ZonedDateTime fecmodi;
}
