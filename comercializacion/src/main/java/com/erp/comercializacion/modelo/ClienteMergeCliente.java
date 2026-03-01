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
@Table(name = "cliente_merge_clientes")
public class ClienteMergeCliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_merge", nullable = false)
    private Long idMerge;

    @Column(name = "cliente_dup_id", nullable = false)
    private Long clienteDupId;

    @Column(name = "fecha_merge", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public ClienteMergeCliente() {}

    public ClienteMergeCliente(Long idMerge, Long clienteDupId) {
        this.idMerge = idMerge;
        this.clienteDupId = clienteDupId;
    }
}

