package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

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
@Table(name="aguatramite")
public class AguaTramite {
   
   @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idaguatramite;
   private String codmedidor;
   private String comentario;
   private Integer estado;
   private String sistema;
   private Date fechaterminacion;
   private String observacion;
   private Long idfactura_facturas; //No todos los trámites tienen factura
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente_clientes")
	private Clientes idcliente_clientes;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtipotramite_tipotramite")
	private TipoTramite idtipotramite_tipotramite;
   private Long usucrea; 
   private Date feccrea;
	private Long usumodi;
   private Date fecmodi;
   private Long iddocumento_documentos;
   private String nrodocumento;


}
