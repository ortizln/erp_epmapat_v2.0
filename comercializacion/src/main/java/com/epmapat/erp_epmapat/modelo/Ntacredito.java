package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ntacredito")
public class Ntacredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idntacredito;
    private String observacion;
    private BigDecimal valor;
    private BigDecimal devengado;
    private Long idtransfernota;
    private String razontransferencia;
    private String nrofactura;
    private Long usuarioeliminacion;
    private LocalDate fechaeliminacion;
    private String razoneliminacion;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idcliente_clientes")
    private Clientes idcliente_clientes;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado_abonados")
    private Abonados idabonado_abonados;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddocumento_documentos")
    private Documentos iddocumento_documentos;
    private String refdocumento;
}
