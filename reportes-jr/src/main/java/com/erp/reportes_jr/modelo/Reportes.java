package com.erp.reportes_jr.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.Map;
import com.vladmihalcea.hibernate.type.json.JsonType;

@Entity
@Table(name = "reportes")
@Getter
@Setter
@Builder
public class Reportes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idreporte;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "archivo_jasper")
    private byte[] archivoJasper;

    @JdbcTypeCode(Types.BINARY)
    @Column(name = "archivo_jrxml")
    private byte[] archivoJrxml;

    @Type(JsonType.class) // 👈 Esto hace que maneje JSONB correctamente
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> parametros;  // O JsonNode si prefieres

    @Column(name = "creado", updatable = false)
    private LocalDateTime creado = LocalDateTime.now();

}