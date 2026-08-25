package com.erp.sri_files.batch;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "batch_jobs", indexes = {
    @Index(name = "idx_batch_estado", columnList = "estado"),
    @Index(name = "idx_batch_tipo", columnList = "tipo_proceso"),
    @Index(name = "idx_batch_fecha", columnList = "fecha_inicio")
})
public class BatchJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, length = 36)
    private String uuid;

    @Column(name = "tipo_proceso", nullable = false, length = 30)
    private String tipoProceso;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "total_documentos")
    private Integer totalDocumentos;

    @Column(name = "procesados")
    private Integer procesados;

    @Column(name = "exitosos")
    private Integer exitosos;

    @Column(name = "fallidos")
    private Integer fallidos;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "triggered_by", length = 100)
    private String triggeredBy;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (uuid == null) uuid = java.util.UUID.randomUUID().toString();
        if (estado == null) estado = "PENDIENTE";
        if (procesados == null) procesados = 0;
        if (exitosos == null) exitosos = 0;
        if (fallidos == null) fallidos = 0;
    }

    public void incrementarProcesados(boolean exitoso) {
        procesados++;
        if (exitoso) exitosos++;
        else fallidos++;
    }
}
