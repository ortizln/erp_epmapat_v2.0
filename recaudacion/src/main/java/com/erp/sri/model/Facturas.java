package com.erp.sri.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "facturas")
public class Facturas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfactura;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idmodulo")
    private Modulos idmodulo;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente")
    private Clientes idcliente;
    private String nrofactura;
    private Long porcexoneracion;
    private String razonexonera;
    private BigDecimal totaltarifa;
    private Long pagado;
    private Long usuariocobro;
    private LocalDateTime fechacobro;
    private Long estado;
    private Long usuarioanulacion;
    private LocalDate fechaanulacion;
    private String razonanulacion;
    private Long usuarioeliminacion;
    private LocalDate fechaeliminacion;
    private String razoneliminacion;
    private Long conveniopago;
    private LocalDate fechaconvenio;
    private Long estadoconvenio;
    private Long formapago;
    private String refeformapago;
    private String horacobro;
    private Long usuariotransferencia;
    private LocalDate fechatransferencia;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
    private BigDecimal valorbase;
    private Long idabonado;
    private BigDecimal interescobrado;
    private BigDecimal swiva;
    private Boolean swcondonar;    
}
