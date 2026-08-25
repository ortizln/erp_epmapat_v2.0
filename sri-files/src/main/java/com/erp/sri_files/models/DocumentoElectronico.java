package com.erp.sri_files.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documento_electronico", indexes = {
    @Index(name = "idx_doc_external_id", columnList = "external_id"),
    @Index(name = "idx_doc_clave_acceso", columnList = "clave_acceso"),
    @Index(name = "idx_doc_estado", columnList = "estado"),
    @Index(name = "idx_doc_tipo", columnList = "tipo_documento"),
    @Index(name = "idx_doc_fecha_emision", columnList = "fecha_emision"),
    @Index(name = "idx_doc_ruc_emisor", columnList = "ruc_emisor")
})
public class DocumentoElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false, length = 36)
    private String uuid;

    @Column(name = "external_id", length = 100)
    private String externalId;

    @Column(name = "tipo_documento", nullable = false, length = 10)
    private String tipoDocumento;

    @Column(name = "estado", nullable = false, length = 30)
    private String estado;

    @Column(name = "subestado", length = 50)
    private String subestado;

    @Column(name = "ruc_emisor", nullable = false, length = 13)
    private String rucEmisor;

    @Column(name = "establecimiento", length = 3)
    private String establecimiento;

    @Column(name = "punto_emision", length = 3)
    private String puntoEmision;

    @Column(name = "secuencial", length = 9)
    private String secuencial;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Column(name = "clave_acceso", length = 49)
    private String claveAcceso;

    @Column(name = "ambiente")
    private Integer ambiente;

    @Column(name = "fecha_transaccion_origen")
    private LocalDateTime fechaTransaccionOrigen;

    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision;

    @Column(name = "fecha_recepcion_json")
    private LocalDateTime fechaRecepcionJson;

    @Column(name = "fecha_generacion_xml")
    private LocalDateTime fechaGeneracionXml;

    @Column(name = "fecha_firma")
    private LocalDateTime fechaFirma;

    @Column(name = "fecha_envio_sri")
    private LocalDateTime fechaEnvioSri;

    @Column(name = "fecha_autorizacion_sri")
    private LocalDateTime fechaAutorizacionSri;

    @Column(name = "fecha_generacion_ride")
    private LocalDateTime fechaGeneracionRide;

    @Column(name = "fecha_envio_correo")
    private LocalDateTime fechaEnvioCorreo;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "fecha_ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;

    @Column(name = "identificacion_receptor", length = 20)
    private String identificacionReceptor;

    @Column(name = "razon_social_receptor", length = 300)
    private String razonSocialReceptor;

    @Column(name = "email_receptor", length = 200)
    private String emailReceptor;

    @Column(name = "subtotal", precision = 18, scale = 6)
    private BigDecimal subtotal;

    @Column(name = "total_impuestos", precision = 18, scale = 6)
    private BigDecimal totalImpuestos;

    @Column(name = "total_descuento", precision = 18, scale = 6)
    private BigDecimal totalDescuento;

    @Column(name = "importe_total", precision = 18, scale = 6)
    private BigDecimal importeTotal;

    @Column(name = "json_original", columnDefinition = "TEXT")
    private String jsonOriginal;

    @Column(name = "xml_generado", columnDefinition = "TEXT")
    private String xmlGenerado;

    @Column(name = "xml_firmado", columnDefinition = "TEXT")
    private String xmlFirmado;

    @Column(name = "xml_autorizado", columnDefinition = "TEXT")
    private String xmlAutorizado;

    @Column(name = "numero_autorizacion", length = 49)
    private String numeroAutorizacion;

    @Column(name = "mensaje_sri", columnDefinition = "TEXT")
    private String mensajeSri;

    @Column(name = "origen_sistema", length = 50)
    private String origenSistema;

    @Column(name = "id_origen", length = 100)
    private String idOrigen;

    @Column(name = "documento_origen_id")
    private Long documentoOrigenId;

    @Column(name = "documento_sustituto_id")
    private Long documentoSustitutoId;

    @Column(name = "motivo_correccion", columnDefinition = "TEXT")
    private String motivoCorreccion;

    @Column(name = "intentos_envio")
    private Integer intentosEnvio;

    @Column(name = "intentos_autorizacion")
    private Integer intentosAutorizacion;

    @Column(name = "intentos_correo")
    private Integer intentosCorreo;

    @Column(name = "mail_enviado")
    private Boolean mailEnviado;

    @Column(name = "processing_lock")
    private Boolean processingLock;

    @Column(name = "locked_at")
    private LocalDateTime lockedAt;

    @Column(name = "locked_by", length = 100)
    private String lockedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (uuid == null) uuid = java.util.UUID.randomUUID().toString();
        if (estado == null) estado = "RECIBIDO";
        if (intentosEnvio == null) intentosEnvio = 0;
        if (intentosAutorizacion == null) intentosAutorizacion = 0;
        if (intentosCorreo == null) intentosCorreo = 0;
        if (processingLock == null) processingLock = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean intentarLock(String owner) {
        if (Boolean.TRUE.equals(processingLock)) {
            return false;
        }
        this.processingLock = true;
        this.lockedAt = LocalDateTime.now();
        this.lockedBy = owner;
        return true;
    }

    public void liberarLock() {
        this.processingLock = false;
        this.lockedAt = null;
        this.lockedBy = null;
    }
}
