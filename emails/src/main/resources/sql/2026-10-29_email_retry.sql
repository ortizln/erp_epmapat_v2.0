-- Ejecutar antes de desplegar emails (ddl-auto=none).
ALTER TABLE public.email_message ADD COLUMN IF NOT EXISTS next_attempt_at timestamptz;
CREATE INDEX IF NOT EXISTS idx_email_pending_retry
    ON public.email_message (next_attempt_at, created_at) WHERE status = 'PENDING';
