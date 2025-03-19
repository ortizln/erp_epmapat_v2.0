package com.erp.pagosonline.models;

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
@Table(name = "facturas")
public class Facturas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idfactura;
    private Long idmodulo;
    private Long idcliente;
    private String nrofactura;
    private Long porcexoneracion;
    private String razonexonera;
    private BigDecimal totaltarifa;
    private Integer pagado;
    private Long usuariocobro;
    private LocalDate fechacobro;
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
