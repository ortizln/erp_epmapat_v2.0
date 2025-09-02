package com.epmapat.erp_epmapat.modelo.coactivas;

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

import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.modelo.Clientes;
import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "remision")
public class Remision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idremision; 
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente_clientes")
    private Clientes idcliente_clientes; 
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idabonado_abonados")
    private Abonados idabonado_abonados; 
    private Long cuotas; 
    private LocalDate fectopedeuda; 
    private LocalDate fectopepago; 
    private BigDecimal totcapital; 
    private BigDecimal totintereses; 
    private Boolean swconvenio; 
    private Long usucrea; 
    private LocalDate feccrea; 
    private Long usumodi; 
    private LocalDate fecmodi; 
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "iddocumento_documentos")
    private Documentos iddocumento_documentos ;
    private String detalledocumento; 
    private Long idconvenio; 
    
    
}
