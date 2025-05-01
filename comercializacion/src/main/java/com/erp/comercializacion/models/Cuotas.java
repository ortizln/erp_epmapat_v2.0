package com.erp.comercializacion.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cuotas")
public class Cuotas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcuota;
    private Long nrocuota;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idfactura")
    private Facturas idfactura;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="idconvenio_convenios")
    private Convenios idconvenio_convenios;
    private Long usucrea;
    private LocalDate feccrea;

}
