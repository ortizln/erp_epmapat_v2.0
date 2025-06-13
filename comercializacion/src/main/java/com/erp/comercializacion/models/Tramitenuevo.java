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
@Table(name = "tramitenuevo")
public class Tramitenuevo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtramitenuevo;
    private String direccion;
    private String nrocasa;
    private String nrodepar;
    private String referencia;
    private String barrio;
    private Long tipopredio;
    private Long presentacedula;
    private Long presentaescritura;
    private Long solicitaagua;
    private Long solicitaalcantarillado;
    private Long aprobadoagua;
    private Long aprobadoalcantarillado;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechainspeccion")
    private Date fechainspeccion;
    private Long medidorempresa;
    private String medidormarca;
    private String medidornumero;
    private Long medidornroesferas;
    private String tuberiaprincipal;
    private Long tipovia;
    private Long codmedidor;
    private Long codmedidorvecino;
    private Long secuencia;
    private String inspector;
    private Long areaconstruccion;
    private String notificado;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechanotificacion")
    private Date fechanotificacion;
    private String observaciones;
    private Long estado;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechafinalizacion")
    private Date fechafinalizacion;
    private Long medidordiametro;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idcategoria_categorias")
    private Categorias idcategoria_categorias;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idaguatramite_aguatramite")
    private Aguatramite idaguatramite_aguatramite;
    private Long usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
    private Long usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fecmodi")
    private Date fecmodi;
}
