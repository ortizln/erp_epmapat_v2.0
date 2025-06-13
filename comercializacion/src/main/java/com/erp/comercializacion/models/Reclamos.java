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
@Table(name = "reclamos")
public class Reclamos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idreclamo;
    private String observacion;
    private Long referencia;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechaasignacion")
    private Date fechaasignacion;
    private Long estado;
    private String referenciadireccion;
    private String piso;
    private String departamento;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "fechamaxcontesta")
    private Date fechamaxcontesta;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column ( name = "fechacontesta")
    private Date fechacontesta;
    private String contestacion;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column ( name = "fechaterminacion")
    private Date fechaterminacion;
    private String responsablereclamo;
    private String modulo;
    private String notificacion;
    private Long estadonotificacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtpreclamo_tpreclamo")
    private Tpreclamo idtpreclamo_tpreclamo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idmodulo_modulos")
    private Modulos idmodulo_modulos;
    private Long usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @Column(name="feccrea")
    private Date feccrea;
    private Long usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso= DateTimeFormat.ISO.DATE)
    @Column(name="fecmodi")
    private Date fecmodi;
}
