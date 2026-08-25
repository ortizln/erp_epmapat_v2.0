-- Migración: Tabla documento_electronico
-- Fecha: 2026-08-21
-- Descripción: Tabla central para administración integral de comprobantes electrónicos

CREATE TABLE IF NOT EXISTS documento_electronico (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    external_id VARCHAR(100),
    tipo_documento VARCHAR(10) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'RECIBIDO',
    subestado VARCHAR(50),
    
    -- Identificación del comprobante
    ruc_emisor VARCHAR(13) NOT NULL,
    establecimiento VARCHAR(3),
    punto_emision VARCHAR(3),
    secuencial VARCHAR(9),
    numero_documento VARCHAR(20),
    clave_acceso VARCHAR(49),
    ambiente INTEGER,
    
    -- Fechas del proceso
    fecha_transaccion_origen TIMESTAMP,
    fecha_emision TIMESTAMP,
    fecha_recepcion_json TIMESTAMP,
    fecha_generacion_xml TIMESTAMP,
    fecha_firma TIMESTAMP,
    fecha_envio_sri TIMESTAMP,
    fecha_autorizacion_sri TIMESTAMP,
    fecha_generacion_ride TIMESTAMP,
    fecha_envio_correo TIMESTAMP,
    fecha_anulacion TIMESTAMP,
    fecha_ultima_actualizacion TIMESTAMP,
    
    -- Receptor
    identificacion_receptor VARCHAR(20),
    razon_social_receptor VARCHAR(300),
    email_receptor VARCHAR(200),
    
    -- Totales
    subtotal NUMERIC(18,6),
    total_impuestos NUMERIC(18,6),
    total_descuento NUMERIC(18,6),
    importe_total NUMERIC(18,6),
    
    -- Contenido
    json_original TEXT,
    xml_generado TEXT,
    xml_firmado TEXT,
    xml_autorizado TEXT,
    
    -- Respuesta SRI
    numero_autorizacion VARCHAR(49),
    mensaje_sri TEXT,
    
    -- Origen
    origen_sistema VARCHAR(50),
    id_origen VARCHAR(100),
    
    -- Relaciones
    documento_origen_id BIGINT,
    documento_sustituto_id BIGINT,
    motivo_correccion TEXT,
    
    -- Contadores
    intentos_envio INTEGER DEFAULT 0,
    intentos_autorizacion INTEGER DEFAULT 0,
    intentos_correo INTEGER DEFAULT 0,
    
    -- Lock de procesamiento
    processing_lock BOOLEAN DEFAULT FALSE,
    locked_at TIMESTAMP,
    locked_by VARCHAR(100),
    
    -- Auditoría
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100)
);

-- Índices
CREATE INDEX IF NOT EXISTS idx_doc_external_id ON documento_electronico(external_id);
CREATE INDEX IF NOT EXISTS idx_doc_clave_acceso ON documento_electronico(clave_acceso);
CREATE INDEX IF NOT EXISTS idx_doc_estado ON documento_electronico(estado);
CREATE INDEX IF NOT EXISTS idx_doc_tipo ON documento_electronico(tipo_documento);
CREATE INDEX IF NOT EXISTS idx_doc_fecha_emision ON documento_electronico(fecha_emision);
CREATE INDEX IF NOT EXISTS idx_doc_ruc_emisor ON documento_electronico(ruc_emisor);
CREATE INDEX IF NOT EXISTS idx_doc_origen ON documento_electronico(external_id, origen_sistema);
CREATE INDEX IF NOT EXISTS idx_doc_pendientes ON documento_electronico(estado, intentos_autorizacion) WHERE estado IN ('PENDIENTE_AUTORIZACION', 'ENVIANDO_SRI');
CREATE INDEX IF NOT EXISTS idx_doc_lock ON documento_electronico(processing_lock, locked_at) WHERE processing_lock = TRUE;

-- Restricción única para evitar duplicados por origen
-- Aplicar después de revisar datos históricos
-- ALTER TABLE documento_electronico ADD CONSTRAINT uk_doc_origen UNIQUE (external_id, origen_sistema);
-- ALTER TABLE documento_electronico ADD CONSTRAINT uk_doc_clave UNIQUE (clave_acceso);
