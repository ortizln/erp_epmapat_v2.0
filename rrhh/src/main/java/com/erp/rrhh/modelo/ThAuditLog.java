package com.erp.rrhh.modelo;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
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
@Table(name = "th_audit_log")
public class ThAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idaudit;

    private String entidad;
    private Long idregistro;
    private String accion;
    private String detalle;
    private Long usuario;
    private LocalDateTime fecha;

    @PrePersist
    public void prePersist() {
        if (fecha == null) fecha = LocalDateTime.now();
    }
}
