package com.erp.reportes_jr.modelo;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reportes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idreporte;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Lob
    @Column(name = "archivo_jrxml")
    private byte[] archivoJrxml;

    @Lob
    @Column(name = "archivo_jasper")
    private byte[] archivoJasper;

    @Column(columnDefinition = "jsonb")
    private String parametros;

    @Column(name = "creado", updatable = false)
    private LocalDateTime creado = LocalDateTime.now();

}
