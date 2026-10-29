-- Aplicar antes de desplegar msvc-sri; no modifica importes ni recaudacion.
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS xml_firmado_envio text;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS recepcion_confirmada boolean;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS email_message_id uuid;
ALTER TABLE public.fec_factura ADD COLUMN IF NOT EXISTS mail_enviado boolean;
CREATE INDEX IF NOT EXISTS idx_fec_factura_correo_pendiente ON public.fec_factura(idfactura)
WHERE upper(trim(estado)) = 'O';
-- No reenviar historicos A automaticamente: revisar entrega y correlacion antes de recuperarlos.
