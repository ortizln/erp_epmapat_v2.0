-- Migración: Tabla batch_jobs para procesamiento masivo
-- Fecha: 2026-08-21

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

-- Agregar columna mail_enviado si no existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'documento_electronico' AND column_name = 'mail_enviado') THEN
        ALTER TABLE documento_electronico ADD COLUMN mail_enviado BOOLEAN DEFAULT FALSE;
    END IF;
END $$;
