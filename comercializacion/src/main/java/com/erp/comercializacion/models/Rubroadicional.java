package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rubroadicional")
public class Rubroadicional {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrubroadicional;
    private Float valor;
    private Long swiva;
    private Long rubroprincipal;
    private Long usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "feccrea")
    private Date feccrea;
    private Long usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "fecmodi")
    private Date fecmodi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idrubro_rubros")
    private Rubros idrubro_rubros;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtptramite_tptramite")
    private Tptramite idtptramite_tptramite;
}
