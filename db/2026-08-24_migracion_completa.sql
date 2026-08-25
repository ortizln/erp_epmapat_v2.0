-- Migración combinada: documento_electronico + batch_jobs + mail_enviado
-- Fecha: 2026-08-24

-- 1) Tabla documento_electronico
CREATE TABLE IF NOT EXISTS documento_electronico (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    external_id VARCHAR(100),
    tipo_documento VARCHAR(10) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'RECIBIDO',
    subestado VARCHAR(50),
    ruc_emisor VARCHAR(13) NOT NULL,
    establecimiento VARCHAR(3),
    punto_emision VARCHAR(3),
    secuencial VARCHAR(9),
    numero_documento VARCHAR(20),
    clave_acceso VARCHAR(49),
    ambiente INTEGER,
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
    identificacion_receptor VARCHAR(20),
    razon_social_receptor VARCHAR(300),
    email_receptor VARCHAR(200),
    subtotal NUMERIC(18,6),
    total_impuestos NUMERIC(18,6),
    total_descuento NUMERIC(18,6),
    importe_total NUMERIC(18,6),
    json_original TEXT,
    xml_generado TEXT,
    xml_firmado TEXT,
    xml_autorizado TEXT,
    numero_autorizacion VARCHAR(49),
    mensaje_sri TEXT,
    origen_sistema VARCHAR(50),
    id_origen VARCHAR(100),
    documento_origen_id BIGINT,
    documento_sustituto_id BIGINT,
    motivo_correccion TEXT,
    intentos_envio INTEGER DEFAULT 0,
    intentos_autorizacion INTEGER DEFAULT 0,
    intentos_correo INTEGER DEFAULT 0,
    mail_enviado BOOLEAN DEFAULT FALSE,
    processing_lock BOOLEAN DEFAULT FALSE,
    locked_at TIMESTAMP,
    locked_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    created_by VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_doc_external_id ON documento_electronico(external_id);
CREATE INDEX IF NOT EXISTS idx_doc_clave_acceso ON documento_electronico(clave_acceso);
CREATE INDEX IF NOT EXISTS idx_doc_estado ON documento_electronico(estado);
CREATE INDEX IF NOT EXISTS idx_doc_tipo ON documento_electronico(tipo_documento);
CREATE INDEX IF NOT EXISTS idx_doc_fecha_emision ON documento_electronico(fecha_emision);
CREATE INDEX IF NOT EXISTS idx_doc_ruc_emisor ON documento_electronico(ruc_emisor);

-- 2) Tabla batch_jobs
CREATE TABLE IF NOT EXISTS batch_jobs (
    id BIGSERIAL PRIMARY KEY,
    uuid VARCHAR(36) NOT NULL UNIQUE,
    tipo_proceso VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    total_documentos INTEGER,
    procesados INTEGER DEFAULT 0,
    exitosos INTEGER DEFAULT 0,
    fallidos INTEGER DEFAULT 0,
    mensaje TEXT,
    triggered_by VARCHAR(100),
    fecha_inicio TIMESTAMP,
    fecha_fin TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_batch_estado ON batch_jobs(estado);
CREATE INDEX IF NOT EXISTS idx_batch_tipo ON batch_jobs(tipo_proceso);
CREATE INDEX IF NOT EXISTS idx_batch_fecha ON batch_jobs(fecha_inicio);

-- 3) Agregar mail_enviado a fec_factura si no existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'fec_factura' AND column_name = 'mail_enviado') THEN
        ALTER TABLE fec_factura ADD COLUMN mail_enviado BOOLEAN DEFAULT FALSE;
    END IF;
END $$;
