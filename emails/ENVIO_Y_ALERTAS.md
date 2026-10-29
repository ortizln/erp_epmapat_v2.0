# Envio y alertas de emails

El flujo completo y las instrucciones actualizadas estan en
[../sri-files/CICLO_FACTURAS_CONFIABLE.md](../sri-files/CICLO_FACTURAS_CONFIABLE.md).

La prueba real de Telegram realizada el 29 de octubre de 2026 fue aceptada por el bot.
La configuracion proporcionada esta en .env.telegram.local, excluido mediante
.git/info/exclude. No se ha desplegado la nueva version en produccion.

Errores ERROR posteriores al arranque se notifican mediante un hilo independiente,
cola de 50 mensajes y limite de una alerta por logger cada 300 segundos. El ID de
correo se incluye cuando esta disponible; se omiten los argumentos con datos personales.
Las fallas Telegram no registran URL/token y no bloquean el procesamiento. No se cubren
caidas completas del proceso ni errores previos a registrar el componente de alertas.

Antes de arrancar emails aplicar src/main/resources/sql/2026-10-29_email_retry.sql.
Las facturas DOC_ELECTRONICO reintentan sin limite de cantidad para fallos de envio,
con espera exponencial de 30s hasta 1h; lista negra y adjuntos perdidos quedan FAILED.
Los otros tipos mantienen max-attempts. POST /api/v1/emails/{id}/retry solo acepta FAILED.
GET /api/v1/emails/{id}/status devuelve el estado sin cuerpo ni adjuntos.
SMTP usa email_account en BD: corregir alli el rechazo 535 del log.
SENT confirma aceptacion del proveedor y requiere seguimiento de rebotes para entrega final.

Telegram API: https://core.telegram.org/bots/api#sendmessage

## Migracion automatica de la cola

Desde esta correccion, Spring SQL init ejecuta sql/2026-10-29_email_retry.sql
sobre el DataSource efectivo antes de inicializar JPA y los schedulers. Crea
next_attempt_at e indice si faltan. Si no tiene permisos o falta email_message,
el arranque falla para impedir que se anuncie operativo con la cola bloqueada.
El usuario de BD necesita permisos ALTER sobre email_message. No modifica filas.
Si Config Server define spring.sql.init.*, conservar mode=always,
continue-on-error=false y la ubicacion del script de esta version.
