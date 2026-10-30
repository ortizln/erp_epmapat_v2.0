ALTER TABLE public.email_message ADD COLUMN IF NOT EXISTS processing_token uuid;
ALTER TABLE public.email_message ADD COLUMN IF NOT EXISTS processing_until timestamptz;
ALTER TABLE public.email_message ADD COLUMN IF NOT EXISTS failure_kind varchar(40);
CREATE TABLE IF NOT EXISTS public.correo_intento (
 id bigserial PRIMARY KEY, email_id uuid NOT NULL REFERENCES public.email_message(id),
 token uuid NOT NULL UNIQUE, intento integer NOT NULL, resultado varchar(40) NOT NULL DEFAULT 'SENDING',
 iniciado timestamptz NOT NULL DEFAULT now(), terminado timestamptz, error text);
CREATE INDEX IF NOT EXISTS correo_intento_email ON public.correo_intento(email_id,id);
CREATE TABLE IF NOT EXISTS public.correo_operacion (
 id bigserial PRIMARY KEY,email_id uuid NOT NULL,actor varchar(100) NOT NULL,
 accion varchar(50) NOT NULL,detalle text,creado timestamptz NOT NULL DEFAULT now());

ALTER TABLE public.email_account ADD COLUMN IF NOT EXISTS last_connection_check timestamptz;
ALTER TABLE public.email_account ADD COLUMN IF NOT EXISTS last_connection_success boolean;

ALTER TABLE public.email_message DROP CONSTRAINT IF EXISTS email_message_status_check;
ALTER TABLE public.email_message ADD CONSTRAINT email_message_status_check
 CHECK (status IN ('PENDING','SENT','FAILED','UNCERTAIN','CANCELLED')) NOT VALID;
ALTER TABLE public.email_message VALIDATE CONSTRAINT email_message_status_check;
CREATE INDEX IF NOT EXISTS email_message_correlation ON public.email_message(type,correlation_id);
