ALTER TABLE fec_factura
ADD COLUMN IF NOT EXISTS intentos_autorizacion INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS fecha_ultimo_intento TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS fecha_autorizacion TIMESTAMP NULL,
ADD COLUMN IF NOT EXISTS mail_enviado BOOLEAN DEFAULT FALSE;

UPDATE fec_factura
SET intentos_autorizacion = COALESCE(intentos_autorizacion, 0),
    mail_enviado = COALESCE(mail_enviado, FALSE)
WHERE intentos_autorizacion IS NULL
   OR mail_enviado IS NULL;

CREATE TABLE IF NOT EXISTS fec_factura_log (
    id BIGSERIAL PRIMARY KEY,
    idfactura BIGINT NOT NULL,
    estado VARCHAR(5) NOT NULL,
    mensaje TEXT,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
