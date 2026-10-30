-- Aditiva e idempotente. Los estados ERP existentes se conservan.
ALTER TABLE public.fec_factura ALTER COLUMN xmlautorizado TYPE text;
ALTER TABLE public.fec_factura ALTER COLUMN errores TYPE text;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS xml_firmado_envio text;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS recepcion_confirmada boolean;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS email_message_id uuid;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS mail_enviado boolean;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS notificacion_version integer NOT NULL DEFAULT 1;
CREATE TABLE IF NOT EXISTS public.sri_autorizacion_control (
 factura_id bigint PRIMARY KEY REFERENCES public.fec_factura(idfactura),
 clave_acceso varchar(49) NOT NULL, estado varchar(40) NOT NULL,
 actualizado timestamptz NOT NULL DEFAULT now(), ultimo_error text,
 intentos integer NOT NULL DEFAULT 0, proxima_consulta timestamptz);
ALTER TABLE public.sri_autorizacion_control ADD COLUMN IF NOT EXISTS intentos integer NOT NULL DEFAULT 0;
ALTER TABLE public.sri_autorizacion_control ADD COLUMN IF NOT EXISTS proxima_consulta timestamptz;
CREATE TABLE IF NOT EXISTS public.comprobante_documento (
 factura_id bigint PRIMARY KEY REFERENCES public.fec_factura(idfactura),
 xml_autorizado text NOT NULL, xml_sha256 varchar(64) NOT NULL,
 ride bytea, ride_sha256 varchar(64), generado timestamptz NOT NULL DEFAULT now());
CREATE TABLE IF NOT EXISTS public.evento_outbox (
 id bigserial PRIMARY KEY, factura_id bigint NOT NULL REFERENCES public.fec_factura(idfactura),
 tipo varchar(50) NOT NULL DEFAULT 'NOTIFICAR_FACTURA', version integer NOT NULL DEFAULT 1,
 estado varchar(30) NOT NULL DEFAULT 'PENDING', intentos integer NOT NULL DEFAULT 0,
 proximo_intento timestamptz NOT NULL DEFAULT now(), token uuid, reservado_hasta timestamptz,
 ultimo_error text, creado timestamptz NOT NULL DEFAULT now(), actualizado timestamptz NOT NULL DEFAULT now(),
 UNIQUE(factura_id,tipo,version));
CREATE INDEX IF NOT EXISTS evento_outbox_pendiente ON public.evento_outbox(proximo_intento,id)
 WHERE estado IN ('PENDING','RETRY','WAITING_EMAIL','PROCESSING');
CREATE TABLE IF NOT EXISTS public.factura_operacion (
 id bigserial PRIMARY KEY, factura_id bigint NOT NULL, actor varchar(100) NOT NULL,
 accion varchar(80) NOT NULL, detalle text, creado timestamptz NOT NULL DEFAULT now());
