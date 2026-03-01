package com.erp.comercializacion.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "cliente_merge_facturas")
public class ClienteMergeFactura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_merge", nullable = false)
    private Long idMerge;

    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    @Column(name = "cliente_origen", nullable = false)
    private Long clienteOrigen;

    @Column(name = "fecha_merge", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public ClienteMergeFactura() {}

    public ClienteMergeFactura(Long idMerge, Long facturaId, Long clienteOrigen) {
        this.idMerge = idMerge;
        this.facturaId = facturaId;
        this.clienteOrigen = clienteOrigen;
    }
}

