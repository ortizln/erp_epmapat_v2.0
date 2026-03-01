package com.erp.modelo;

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
@Table(name = "cliente_merge_abonados")
public class ClienteMergeAbonado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_merge", nullable = false)
    private Long idMerge;

    @Column(name = "abonado_id", nullable = false)
    private Long abonadoId;

    @Column(name = "cliente_origen", nullable = false)
    private Long clienteOrigen;

    @Column(name = "fecha_merge", nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    public ClienteMergeAbonado() {}

    public ClienteMergeAbonado(Long idMerge, Long abonadoId, Long clienteOrigen) {
        this.idMerge = idMerge;
        this.abonadoId = abonadoId;
        this.clienteOrigen = clienteOrigen;
    }
}
